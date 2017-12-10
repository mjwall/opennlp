/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.demo.resource;

import java.io.IOException;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class VersionTest extends JerseyTest {

  @Override
  protected Application configure() {
    return new ResourceConfig(Version.class);
  }

  @Test
  public void testGetVersion() {
    Response response = target("/version").request().get();
    assertThat(response.getStatus(), equalTo(200));
    assertThat(response.getMediaType().toString(), equalTo(MediaType.TEXT_PLAIN));
    assertThat(response.readEntity(String.class), containsString("OpenNLP Version"));
  }

  @Test
  public void testGetVersionJson() throws IOException {
    Response response = target("/version.json").request().get();
    assertThat(response.getStatus(), equalTo(200));
    assertThat(response.getMediaType().toString(), equalTo(MediaType.APPLICATION_JSON));
    ObjectMapper mapper = new ObjectMapper();
    JsonNode json = mapper.readTree(response.readEntity(String.class));
    assertThat(json.has("version"), is(true));
    assertThat(json.get("version").toString(), is(not(emptyString())));
  }
}
