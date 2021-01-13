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
        writeCache = new WriteCache(byteBufAllocator, entrySize * entryNumber, 1024);
        entry = byteBufAllocator.buffer(entrySize);
        ByteBufUtil.writeAscii(entry, "test");
        entry.writerIndex(entry.capacity());
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

    @Parameterized.Parameters
    public static Collection<Object[]> WriteCacheGetParameters(){
        return Arrays.asList(new Object[][] {
        	//suite minimale
            {new WriteTestEntity(0, 0), null}, //sovrascritto dal setup: ByteBuff immesso in cache
            {new WriteTestEntity(-1, -1), null},
            {new WriteTestEntity(1, 1), null},
            
            //coverage
            {new WriteTestEntity(0, -1), null},
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
            result = null;
        }
        Assert.assertEquals(expectedResults, result);

    }


}