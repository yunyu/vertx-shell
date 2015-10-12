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

package io.vertx.ext.shell.registry.impl;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.shell.session.Session;
import io.vertx.ext.shell.cli.CliToken;
import io.vertx.ext.shell.cli.Completion;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.registry.CommandRegistry;
import io.vertx.ext.shell.registry.CommandRegistration;
import io.vertx.ext.shell.process.Process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class CommandRegistryImpl extends AbstractVerticle implements CommandRegistry {

  private static Map<Vertx, CommandRegistryImpl> registries = new ConcurrentHashMap<>();

  public static CommandRegistry get(Vertx vertx) {



    return registries.computeIfAbsent(vertx, CommandRegistryImpl::new);
  }

  private final Vertx vertx;
  private final ConcurrentHashMap<String, CommandRegistration> commandMap = new ConcurrentHashMap<>();
  private volatile boolean closed;

  public CommandRegistryImpl(Vertx vertx) {

    // The registry can be removed either on purpose or when Vert.x close
    vertx.deployVerticle(this, ar -> {
      if (!ar.succeeded()) {
        registries.remove(vertx);
      }
    });

    this.vertx = vertx;
  }

  @Override
  public void stop() throws Exception {
    closed = true;
    registries.remove(vertx);
  }

  public boolean isClosed() {
    return closed;
  }

  public List<CommandRegistration> registrations() {
    return new ArrayList<>(commandMap.values());
  }

  @Override
  public void registerCommand(Class<? extends Command> command) {
    registerCommand(Command.create(command));
  }

  @Override
  public void registerCommand(Class<? extends Command> command, Handler<AsyncResult<CommandRegistration>> doneHandler) {
    registerCommand(Command.create(command), doneHandler);
  }

  @Override
  public void registerCommand(Command command) {
    registerCommand(command, ar -> {
    });
  }

  @Override
  public void registerCommand(Command command, Handler<AsyncResult<CommandRegistration>> doneHandler) {
    Context context = vertx.getOrCreateContext();
    CommandRegistrationImpl registration = new CommandRegistrationImpl(vertx, context, command);
    String name = command.name();
    if (commandMap.containsKey(name)) {
      doneHandler.handle(Future.failedFuture("Command " + name + " already registered"));
    } else {
      commandMap.put(name, registration);
      doneHandler.handle(Future.succeededFuture(registration));
    }
  }

  @Override
  public void unregisterCommand(String commandName) {
    unregisterCommand(commandName, done -> {});
  }

  @Override
  public void unregisterCommand(String name, Handler<AsyncResult<Void>> doneHandler) {
    if (commandMap.remove(name) != null) {
      doneHandler.handle(Future.succeededFuture());
    } else {
      doneHandler.handle(Future.failedFuture("Command " + name + " not registered"));
    }
  }

  public CommandRegistration getCommand(String name) {
    return commandMap.get(name);
  }

  @Override
  public void createProcess(String line, Handler<AsyncResult<io.vertx.ext.shell.process.Process>> handler) {
    createProcess(CliToken.tokenize(line), handler);
  }

  @Override
  public void createProcess(List<CliToken> line, Handler<AsyncResult<Process>> handler) {
    Process process;
    try {
      process = makeRequest(line);
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
      return;
    }
    handler.handle(Future.succeededFuture(process));
  }

  private Process makeRequest(List<CliToken> s) {
    ListIterator<CliToken> tokens = s.listIterator();
    while (tokens.hasNext()) {
      CliToken token = tokens.next();
      if (token.isText()) {
        CommandRegistration command = getCommand(token.value());
        if (command == null) {
          throw new IllegalArgumentException(token.value() + ": command not found");
        }
        List<CliToken> remaining = new ArrayList<>();
        while (tokens.hasNext()) {
          remaining.add(tokens.next());
        }
        return command.createProcess(remaining);
      }
    }
    throw new IllegalArgumentException();
  }

  @Override
  public void complete(Completion completion) {
    LinkedList<CliToken> tokens = new LinkedList<>(completion.lineTokens());

    // Remove any leading white space
    while (tokens.size() > 0 && tokens.getFirst().isBlank()) {
      tokens.removeFirst();
    }

    // > 1 means it's a text token followed by something else
    if (tokens.size() > 1) {
      ListIterator<CliToken> it = tokens.listIterator();
      while (it.hasNext()) {
        CliToken ct = it.next();
        it.remove();
        if (ct.isText()) {
          List<CliToken> newTokens = new ArrayList<>();
          while (it.hasNext()) {
            newTokens.add(it.next());
          }
          StringBuilder tmp = new StringBuilder();
          newTokens.stream().forEach(token -> tmp.append(token.raw()));
          String line = tmp.toString();
          CommandRegistration command = getCommand(ct.value());
          if (command != null) {
            command.complete(new Completion() {
              @Override
              public Vertx vertx() {
                return completion.vertx();
              }
              @Override
              public Session session() {
                return completion.session();
              }
              @Override
              public String rawLine() {
                return line;
              }
              @Override
              public List<CliToken> lineTokens() {
                return newTokens;
              }
              @Override
              public void complete(List<String> candidates) {
                completion.complete(candidates);
              }
              @Override
              public void complete(String value, boolean terminal) {
                completion.complete(value, terminal);
              }
            });
          } else {
            completion.complete(Collections.emptyList());
          }
        }
      }
    } else {
      String prefix = tokens.size() > 0 ? tokens.getFirst().value() : "";
      List<String> names = registrations().
          stream().
          map(cmd -> cmd.command().name()).
          filter(name -> name.startsWith(prefix)).
          collect(Collectors.toList());
      if (names.size() == 1) {
        completion.complete(names.get(0).substring(prefix.length()), true);
      } else {
        String commonPrefix = Completion.findLongestCommonPrefix(names);
        if (commonPrefix.length() > prefix.length()) {
          completion.complete(commonPrefix.substring(prefix.length()), false);
        } else {
          completion.complete(names);
        }
      }
    }
  }
}
