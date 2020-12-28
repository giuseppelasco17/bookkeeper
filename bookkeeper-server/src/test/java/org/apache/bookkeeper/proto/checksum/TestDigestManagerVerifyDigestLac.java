/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.bookkeeper.proto.checksum;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.bookkeeper.client.BKException.BKDigestMatchException;
import org.apache.bookkeeper.proto.DataFormats.LedgerMetadataFormat.DigestType;
import org.apache.bookkeeper.proto.checksum.entity.DigestManagerEntity;
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
public class TestDigestManagerVerifyDigestLac {

	private Object expectedResult;
	private static int length = 10;
	private DigestManager digest;
	private DigestManager digestForSending;

	private DigestManagerEntity entity;
	private ByteBuf testBuf;

	@Parameterized.Parameters
	public static Collection BufferedChannelParameters() throws Exception {
		return Arrays.asList(new Object[][] {
			
			// Suite minimale
			{null, NullPointerException.class},
			{new DigestManagerEntity(-1, DigestType.CRC32, 1, true), (long)-1},
			
			// Coverage and mutation
			{new DigestManagerEntity(0, DigestType.HMAC,1, false), BKDigestMatchException.class},
			{new DigestManagerEntity(1, DigestType.CRC32C, 1, true), BKDigestMatchException.class},
			{new DigestManagerEntity(-1, DigestType.CRC32, 0, true), BKDigestMatchException.class},

			// Mutazioni
			//{new DigestManagerEntity(0, DigestType.HMAC,1, true), BKDigestMatchException.class},
		});
	}

	public TestDigestManagerVerifyDigestLac(DigestManagerEntity entity, Object expectedResult){
		this.entity=entity;
		this.expectedResult = expectedResult;
	}

	@Before
	public void makeDigestManager() throws GeneralSecurityException {
		this.digest = DigestManager.instantiate(1, "testPassword".getBytes(), 
				DigestType.CRC32, UnpooledByteBufAllocator.DEFAULT, false);
		
		if (entity!=null) {
		
			this.digestForSending = DigestManager.instantiate(entity.getLedgerID(), "testPassword".getBytes(), 
					entity.getDigestType(), UnpooledByteBufAllocator.DEFAULT, entity.isUseV2Protocol());
			
			entity.setTestBufList(digestForSending.computeDigestAndPackageForSendingLac(entity.getLacID()));
		}
	}

	@Test
	public void testVerifyDigestLac() throws GeneralSecurityException {
		

		try {
			System.out.println("READEBLEASS: "+entity.getTestBufList().getBuffer(0));
			Assert.assertEquals(expectedResult, digest.verifyDigestAndReturnLac(entity.getTestBufList().getBuffer(0)));
		} catch (Exception e) {
			Assert.assertEquals(expectedResult, e.getClass());
		}
	}
	
}  


