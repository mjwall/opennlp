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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("") // DemoApplication.REST_CONTEXT or /rest/v1/
public class Version {

  @GET
  @Path("/version")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getVersion() {
    return Response.ok().entity(new opennlp.demo.model.Version().getVersion()).build();
  }

  @GET
  @Path("/version.json")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getVersionJson() {
    String versionString = new opennlp.demo.model.Version().getVersion();
    String version = versionString.replace("OpenNLP Version: ", "");
    Map theMap = new HashMap();
    theMap.put("version", version);
    return Response.ok().entity(theMap).build();
  }

}
