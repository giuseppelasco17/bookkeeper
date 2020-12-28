package org.apache.bookkeeper.proto.checksum;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.apache.bookkeeper.proto.DataFormats.LedgerMetadataFormat.DigestType;
import org.apache.bookkeeper.proto.checksum.entity.DigestManagerEntity;
import org.apache.bookkeeper.client.BKException.BKDigestMatchException;
import org.apache.bookkeeper.util.ByteBufList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;

@RunWith(Parameterized.class)
public class TestDigestManagerVerifyDigest {

	private Object expectedResult;
	
	private DigestManagerEntity entity;
	

	private DigestManager digestManager;
	private DigestManager digestForSending;
	private boolean checked = false;
	
	//private String dataReceived = "";
	//private int lenghtDataReceived = dataReceived.length();
	
    
    public void generatesByteBuf(int lenght) {
    	if (!checked) {
    		Random rnd = new Random();
        	byte[] data = new byte[lenght];
    		rnd.nextBytes(data);
    		
    		ByteBuf bb = Unpooled.buffer(DigestManager.METADATA_LENGTH);
    		bb.writeBytes(data);
    		this.entity.setTestBuf(bb);
    		this.checked = true;
		}
    	
		
		//byte[] data = dataReceived.getBytes();
		
	}
    
   
	
	@Parameterized.Parameters
	public static Collection<Object[]> BufferedChannelParameters() throws Exception {
		return Arrays.asList(new Object[][] {
			
			// Suite minimale
			{new DigestManagerEntity(0, -1, DigestType.HMAC, DigestType.HMAC, 0, -1, false, 12, true), NullPointerException.class},
			{new DigestManagerEntity(0, 1, DigestType.HMAC, DigestType.HMAC, 0, 1, false, 12, false), 0},
			/*l'unico modo per rendere Invalid l'oggeto ByteBufe è modificare il parametro "lenght". L'oggetto BytebUf, però, se riceve un lenght positivo
			 * viene creato correttamente altrimenti genera un'eccezione al momento dell'istanziazione. Quindi, il parametro ByteBuf deve essere passato
			 * già in maniera corretta alla funzione da testare. Questo ci permette di non considerare il caso di test in cui il parametro ByteBuf è
			 * NON valido. */
			
			// Coverage
			{new DigestManagerEntity(0, 1, DigestType.HMAC, DigestType.HMAC, 0, 0, false, 12, false), BKDigestMatchException.class},//entry
			{new DigestManagerEntity(1, 1, DigestType.DUMMY, DigestType.DUMMY, -1, 1, false, 12, false), BKDigestMatchException.class},//ledger
			{new DigestManagerEntity(1, 1, DigestType.HMAC, DigestType.CRC32, 1,1, false, 12, false), BKDigestMatchException.class},//mac
			{new DigestManagerEntity(1, 1, DigestType.HMAC, DigestType.CRC32C,1,1 ,true, 12, false), BKDigestMatchException.class},
			
			//mutation
			{new DigestManagerEntity(1, 1, DigestType.CRC32C, DigestType.CRC32,1,1 ,false, 0, false), BKDigestMatchException.class},
			{new DigestManagerEntity(0, 1, DigestType.HMAC, DigestType.HMAC, 0, 1, false, 0, false), 0},

			
			//{new DigestManagerEntity(1, 1, DigestType.HMAC, DigestType.CRC32C, 1,1, false, 5, false), BKDigestMatchException.class},
			//{new DigestManagerEntity(1, 1, DigestType.CRC32,DigestType.CRC32, 1,1, false, 5, false), 0},
			//{new DigestManagerEntity(1, 1, DigestType.CRC32, DigestType.CRC32,1,0, false, 5, false), BKDigestMatchException.class},
			// Coverage
			
			
		});
	}

	public TestDigestManagerVerifyDigest(DigestManagerEntity entity, Object expectedResult){
		this.entity = entity;
		if (expectedResult.equals(0)) {
			generatesByteBuf(entity.getLength());
			this.expectedResult = entity.getTestBuf();
		}else {
			this.expectedResult = expectedResult;
		}
	}


	@Before
	public void beforeTest() throws GeneralSecurityException {
		this.digestManager = DigestManager.instantiate(entity.getLedgerIdToTest(), 
				"testPassword".getBytes(), entity.getDigestTypeToTest(), UnpooledByteBufAllocator.DEFAULT, false);

		this.digestForSending = DigestManager.instantiate(entity.getLedgerID(), 
				"testPassword".getBytes(), entity.getDigestType(), UnpooledByteBufAllocator.DEFAULT, false);

		generatesByteBuf(entity.getLength());
		ByteBuf bb = this.entity.getTestBuf();
		
		//return bb;
		if (entity.isBadByteBufList()) {
			/*creo un header dopve non scrivo nulla dentro, quindi i byte leggibili sono 0*/
		    ByteBuf badHeader = Unpooled.buffer(DigestManager.METADATA_LENGTH);
		    //badHeader.writeBytes(MUTATION_BYTE_BUF);
		    entity.setTestBufList(ByteBufList.get(badHeader, bb));
		    return;
		}
		if (entity.isNull()) {
			entity.setTestBufList(null);
			return;
		}
		//old lenght=5
		ByteBufList byteBufList = digestForSending.computeDigestAndPackageForSending(entity.getEntryId(), 
				0, this.entity.getTestBuf().readableBytes(), bb);
		this.entity.setTestBufList(byteBufList);

	}
	
	@Test
	public void testVerifyDigestData() throws GeneralSecurityException{

		try {
			Assert.assertEquals(expectedResult, digestManager.verifyDigestAndReturnData(entity.getEntryIdToTest(), 
					ByteBufList.coalesce(entity.getTestBufList())));
		} catch (Exception e) {
			Assert.assertEquals(expectedResult, e.getClass());
		}
	}

}  


