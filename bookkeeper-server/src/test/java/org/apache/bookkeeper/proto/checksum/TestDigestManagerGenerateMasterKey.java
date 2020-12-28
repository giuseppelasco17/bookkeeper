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

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.apache.bookkeeper.proto.checksum.entity.DigestManagerEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;




@RunWith(Parameterized.class)
public class TestDigestManagerGenerateMasterKey {
	

	private byte[] pass;

	private Object expectedResult;
	private static int stringLenght = 50;
	static byte[] b = null;
	
	public static void generatesBytes() {
		b = new byte[stringLenght];
		Random rnd = new Random();
		rnd.nextBytes(b);
	}

	@Parameterized.Parameters
	public static Collection BufferedChannelParameters() throws Exception {
		generatesBytes();
		System.out.println(Arrays.toString(b)+", "+ b.length);
		
		/*MUTATION COVERAGE:
		 * 
		 * - far funzionare il power ìmock al progetto
		 * */
		
		return Arrays.asList(new Object[][] {
			
			// Suite minimale
			{new DigestManagerEntity("".getBytes()), MacDigestManager.EMPTY_LEDGER_KEY},
			{new DigestManagerEntity(b), MacDigestManager.genDigest("ledger", b)}
			

		});
	}
	
	public TestDigestManagerGenerateMasterKey(DigestManagerEntity entity, Object expectedResult){
		if (entity!=null)
			this.pass = entity.getPass();
		this.expectedResult = expectedResult;
	}

	@Test
	public void testGenerateMasterKey() {
		/*byte[] d = Mockito.spy(pass);
		Mockito.when(d.length).thenReturn(1);*/

		try {
			Assert.assertArrayEquals((byte[]) expectedResult, DigestManager.generateMasterKey(pass));
		} catch (Exception e) {
		}
		
	}
	
	public static void main(String[] args) {
		/*byte[] pass = "ciao".getBytes();
		byte[] d = Mockito.spy(pass);
		Mockito.when(d.length).thenReturn(1);
		
		System.out.println(d.length);*/
	}

}  


