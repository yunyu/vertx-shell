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

package io.vertx.rxjava.ext.shell.system;

import java.util.Map;
import io.vertx.lang.rxjava.InternalHelper;
import rx.Observable;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.shell.term.Tty;
import io.vertx.rxjava.ext.shell.session.Session;

/**
 * A process managed by the shell.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.shell.system.Process original} non RX-ified interface using Vert.x codegen.
 */

public class Process {

  final io.vertx.ext.shell.system.Process delegate;

  public Process(io.vertx.ext.shell.system.Process delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * Set the process tty.
   * @param tty the process tty
   * @return this object
   */
  public Process setTty(Tty tty) { 
    this.delegate.setTty((io.vertx.ext.shell.term.Tty) tty.getDelegate());
    return this;
  }

  /**
   * @return the process tty
   * @return 
   */
  public Tty getTty() { 
    Tty ret= Tty.newInstance(this.delegate.getTty());
    return ret;
  }

  /**
   * Set the process session
   * @param session the process session
   * @return this object
   */
  public Process setSession(Session session) { 
    this.delegate.setSession((io.vertx.ext.shell.session.Session) session.getDelegate());
    return this;
  }

  /**
   * @return the process session
   * @return 
   */
  public Session getSession() { 
    Session ret= Session.newInstance(this.delegate.getSession());
    return ret;
  }

  /**
   * Set an handler called when the process terminates.
   * @param handler the terminate handler
   * @return this object
   */
  public Process terminateHandler(Handler<Integer> handler) { 
    this.delegate.terminateHandler(handler);
    return this;
  }

  /**
   * Run the process.
   */
  public void run() { 
    this.delegate.run();
  }

  /**
   * Attempt to interrupt the process.
   * @return true if the process caught the signal
   */
  public boolean interrupt() { 
    boolean ret = this.delegate.interrupt();
    return ret;
  }

  /**
   * Suspend the process.
   */
  public void resume() { 
    this.delegate.resume();
  }

  /**
   * Resume the process.
   */
  public void suspend() { 
    this.delegate.suspend();
  }

  /**
   * Terminate the process.
   */
  public void terminate() { 
    this.delegate.terminate();
  }


  public static Process newInstance(io.vertx.ext.shell.system.Process arg) {
    return arg != null ? new Process(arg) : null;
  }
}
