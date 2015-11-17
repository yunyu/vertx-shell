/*
 * Copyright 2015 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 *
 *
 * Copyright (c) 2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 *
 */

package io.vertx.ext.shell.term;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.shell.term.impl.SockJSTermHandlerImpl;
import io.vertx.ext.shell.term.impl.WebTermServer;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@VertxGen
public interface SockJSTermHandler extends Handler<SockJSSocket> {

  static Buffer defaultTermScriptResource() {
    return WebTermServer.loadResource("/io/vertx/ext/shell/term.js");
  }

  static Buffer defaultTermMarkupResource() {
    return WebTermServer.loadResource("/io/vertx/ext/shell/term.html");
  }

  static SockJSTermHandler create(Vertx vertx) {
    return new SockJSTermHandlerImpl(vertx);
  }

  @Fluent
  SockJSTermHandler termHandler(Handler<Term> handler);

}
