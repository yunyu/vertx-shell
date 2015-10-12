/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.groovy.ext.shell.io;
import groovy.transform.CompileStatic
import io.vertx.lang.groovy.InternalHelper
import io.vertx.core.json.JsonObject
import io.vertx.core.Handler
/**
 * Provide interactions with the Shell TTY.
*/
@CompileStatic
public class Tty {
  private final def io.vertx.ext.shell.io.Tty delegate;
  public Tty(Object delegate) {
    this.delegate = (io.vertx.ext.shell.io.Tty) delegate;
  }
  public Object getDelegate() {
    return delegate;
  }
  /**
   * @return the current width, i.e the number of rows or  if unknown
   * @return 
   */
  public int width() {
    def ret = this.delegate.width();
    return ret;
  }
  /**
   * @return the current height, i.e the number of columns or  if unknown
   * @return 
   */
  public int height() {
    def ret = this.delegate.height();
    return ret;
  }
  /**
   * Set a stream on the standard input to read the data.
   * @param stdin the standard input
   * @return this object
   */
  public Tty setStdin(Stream stdin) {
    this.delegate.setStdin((io.vertx.ext.shell.io.Stream)stdin.getDelegate());
    return this;
  }
  /**
   * Set an handler the standard input to read the data in String format.
   * @param stdin the standard input
   * @return this object
   */
  public Tty setStdin(Handler<String> stdin) {
    this.delegate.setStdin(stdin);
    return this;
  }
  /**
   * @return the standard output for emitting data
   * @return 
   */
  public Stream stdout() {
    def ret= InternalHelper.safeCreate(this.delegate.stdout(), io.vertx.groovy.ext.shell.io.Stream.class);
    return ret;
  }
}
