/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehcache.clustered.server;

import org.ehcache.clustered.common.internal.messages.ClientIDTrackerMessage;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityMessage;
import org.ehcache.clustered.common.internal.messages.LifecycleMessage;
import org.ehcache.clustered.common.internal.messages.ServerStoreOpMessage;
import org.ehcache.clustered.common.internal.messages.StateRepositoryOpMessage;
import org.ehcache.clustered.server.internal.messages.EntitySyncMessage;
import org.terracotta.entity.ExecutionStrategy;

/**
 * EhcacheExecutionStrategy
 */
class EhcacheExecutionStrategy implements ExecutionStrategy<EhcacheEntityMessage> {
  @Override
  public Location getExecutionLocation(EhcacheEntityMessage message) {
    if (message instanceof ServerStoreOpMessage.ReplaceAtHeadMessage || message instanceof ServerStoreOpMessage.ClearMessage) {
      // ServerStoreOp needing replication
      return Location.BOTH;
    } else if (message instanceof ServerStoreOpMessage) {
      // ServerStoreOp not needing replication
      return Location.ACTIVE;
    } else if (message instanceof LifecycleMessage.ConfigureStoreManager) {
      return Location.BOTH;
    } else if (message instanceof LifecycleMessage.ValidateStoreManager) {
      return Location.ACTIVE;
    } else if (message instanceof LifecycleMessage.CreateServerStore) {
      return Location.BOTH;
    } else if (message instanceof LifecycleMessage.ValidateServerStore) {
      return Location.ACTIVE;
    } else if (message instanceof LifecycleMessage.ReleaseServerStore) {
      return Location.ACTIVE;
    } else if (message instanceof LifecycleMessage.DestroyServerStore) {
      return Location.BOTH;
    } else if (message instanceof StateRepositoryOpMessage.PutIfAbsentMessage) {
      // StateRepositoryOp needing replication
      return Location.BOTH;
    } else if (message instanceof StateRepositoryOpMessage) {
      // StateRepositoryOp not needing replication
      return Location.ACTIVE;
    } else if (message instanceof ClientIDTrackerMessage) {
      return Location.PASSIVE;
    } else if (message instanceof EntitySyncMessage) {
      throw new AssertionError("Unexpected use of ExecutionStrategy for sync messages");
    }
    throw new AssertionError("Unknown message type: " + message.getClass());
  }
}
