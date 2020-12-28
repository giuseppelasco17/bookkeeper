package org.apache.bookkeeper.bookie.storage.ldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.entity.WriteTestEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(value = Parameterized.class)
public class WriteCacheGetTest { 

    private WriteCache writeCache;
    private ByteBufAllocator byteBufAllocator;
    private final int entryNumber = 10;
    private final int entrySize = 1024;
    private ByteBuf entry;
    private final int ledgerId = 0;
    private final int entryId = 0;
    private WriteTestEntity writeEntity;
    private ByteBuf expectedResults;


    @Before
    public void setUp() throws Exception {
        byteBufAllocator = UnpooledByteBufAllocator.DEFAULT;
        /*cache formata da segmenti
         * segmenti composti da più entry
         * 
         * piu entry = ledger
         * piu ledgre = bokkie
         * */
        
        /*se mettiamo 1024 come segment max, allora l'd del segmento che andiamo a considerare
         * quando ricerchiamo una entry aumenta.*/
        writeCache = new WriteCache(byteBufAllocator, entrySize * entryNumber, 1024);

        entry = byteBufAllocator.buffer(entrySize);
        //System.out.println(entry.maxCapacity());
        ByteBufUtil.writeAscii(entry, "test");
        entry.writerIndex(entry.capacity());

        
        //System.out.println(writeEntity.getEntryId()+", "+writeEntity.getLedgerId());
        
        if(writeEntity.getLedgerId()>=0 && writeEntity.getEntryId()>=0) {
        	writeCache.put(2, 2, entry);

            writeCache.put(writeEntity.getLedgerId(), writeEntity.getEntryId(), entry);
        	this.expectedResults = entry;
        	
        }

    }

    @After
    public void tearDown() throws Exception {
        writeCache.clear();
        entry.release();
        writeCache.close();
    }

    //Parametri in input
    @Parameterized.Parameters
    public static Collection<?> getParameters(){
        return Arrays.asList(new Object[][] {
        	//key1 >= 0
        	//key1>=0, key1<0
        	//key2>=0, key2<0
        	
        	//key1=-1, key1=0
        	//key2=-1. key2=0
        	//suite minimale
            {new WriteTestEntity(0, 0), null}, //sovrascritto dal setup: ByteBuff immesso in cache
            {new WriteTestEntity(-1, -1), null},
            {new WriteTestEntity(1, 1), null},
            
            //branch e line coverage
            {new WriteTestEntity(0, -1), null},
            //ridondante
            //{new WriteTestEntity(-1, 0), null}
        });
    }

    public WriteCacheGetTest(WriteTestEntity writeEntity, ByteBuf expectedResults){
        this.writeEntity = writeEntity;
        this.expectedResults = expectedResults;
    }
    @Test
    public void getFromCache(){

        ByteBuf result = null;

        try{
            result = writeCache.get(writeEntity.getLedgerId(), writeEntity.getEntryId());
        }
        catch(Exception e){
            //e.printStackTrace();
            result = null;
        }
        
        //System.out.println(expectedResults +", "+result);
        
        Assert.assertEquals(expectedResults, result);

    }


}