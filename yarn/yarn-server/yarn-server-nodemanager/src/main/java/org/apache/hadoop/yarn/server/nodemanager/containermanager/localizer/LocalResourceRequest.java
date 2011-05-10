/**
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer;

import java.net.URISyntaxException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.util.ConverterUtils;

public class LocalResourceRequest implements Comparable<LocalResourceRequest> {

  private final Path loc;
  private final long timestamp;
  private final LocalResourceType type;

  /**
   * Wrap API resource to match against cache of localized resources.
   * @param resource Resource requested by container
   * @throws URISyntaxException If the path is malformed
   */
  public LocalResourceRequest(LocalResource resource)
      throws URISyntaxException {
    this.loc = ConverterUtils.getPathFromYarnURL(resource.getResource());
    this.timestamp = resource.getTimestamp();
    this.type = resource.getType();
  }

  @Override
  public int hashCode() {
    return loc.hashCode() ^
      (int)((timestamp >>> 32) ^ timestamp) *
      type.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LocalResourceRequest)) {
      return false;
    }
    final LocalResourceRequest other = (LocalResourceRequest) o;
    return getPath().equals(other.getPath()) &&
           getTimestamp() == other.getTimestamp() &&
           getType() == other.getType();
  }

  @Override
  public int compareTo(LocalResourceRequest other) {
    if (this == other) {
      return 0;
    }
    int ret = getPath().compareTo(other.getPath());
    if (0 == ret) {
      ret = (int)(getTimestamp() - other.getTimestamp());
      if (0 == ret) {
        ret = getType().ordinal() - other.getType().ordinal();
      }
    }
    return ret;
  }

  public Path getPath() {
    return loc;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public LocalResourceType getType() {
    return type;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ ");
    sb.append(getPath().toString()).append(", ");
    sb.append(getTimestamp()).append(", ");
    sb.append(getType()).append(" }");
    return sb.toString();
  }
}