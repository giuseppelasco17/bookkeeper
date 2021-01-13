package org.apache.bookkeeper.proto.checksum;

import java.security.GeneralSecurityException;
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
public class TestDigestManagerVerifyDigestAndReturnData {

	private Object expectedResult;
	
	private DigestManagerEntity entity;

	private DigestManager digestManager;
	private DigestManager digestForSending;

	private boolean checked = false;
	
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
    }
	
	@Parameterized.Parameters
	public static Collection<Object[]> DigestManagerVerifyDigestAndReturnDataParameters() throws Exception {
		return Arrays.asList(new Object[][] {
			
			// Suite minimale
			{new DigestManagerEntity(0, -1, DigestType.HMAC, DigestType.HMAC, 0, -1, false, 5, true), NullPointerException.class},
			{new DigestManagerEntity(0, 1, DigestType.HMAC, DigestType.HMAC, 0, 1, false, 5, false), 0},
			{new DigestManagerEntity(0, 0, DigestType.HMAC, DigestType.HMAC, 0, 0, false, 5, false), 0},
			//mutation
			{new DigestManagerEntity(1, 1, DigestType.CRC32C, DigestType.CRC32,1,1 ,false, 0, false), BKDigestMatchException.class},
			});
	}
	
	public TestDigestManagerVerifyDigestAndReturnData(DigestManagerEntity entity, Object expectedResult){
		this.entity = entity;
		if (expectedResult.equals(0)) {
			generatesByteBuf(entity.getLength());
			this.expectedResult = entity.getTestBuf();
		}
		else {
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
		
		if (entity.isNull()) {
			entity.setTestBufList(null);
			return;
		}
		
		ByteBufList byteBufList = digestForSending.computeDigestAndPackageForSending(entity.getEntryId(), 0, 
				this.entity.getTestBuf().readableBytes(), bb);
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
