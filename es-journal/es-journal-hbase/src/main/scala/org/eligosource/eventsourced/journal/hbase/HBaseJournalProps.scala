/*
 * Copyright 2012-2013 Eligotech BV.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eligosource.eventsourced.journal.hbase

import scala.concurrent.duration._

import akka.actor.Actor

import org.eligosource.eventsourced.core.JournalProps

/**
 * Configuration object for a [[http://hbase.apache.org/ HBase]] backed journal. Journal
 * actors can be created from a configuration object as follows:
 *
 * {{{
 *  import akka.actor._
 *
 *  import org.eligosource.eventsourced.core.Journal
 *  import org.eligosource.eventsourced.journal.hbase.HBaseJournalProps
 *
 *  implicit val system: ActorSystem = ...
 *
 *  val zookeeperQuorum: String = ... // "localhost", for example
 *  val tableName: String = ...       // "event", for example
 *  val journal: ActorRef = Journal(HBaseJournalProps(zookeeperQuorum))
 * }}}
 *
 * For storing event messages to HBase, an event message table must be initially created. See
 * [[org.eligosource.eventsourced.journal.hbase.CreateTable]] and
 * [[org.eligosource.eventsourced.journal.hbase.DeleteTable]] for details.
 *
 * Event messages will be evenly distributed (partitioned) across table regions.
 *
 * @param zookeeperQuorum Comma separated list of servers in the ZooKeeper quorum.
 *        See also the `hbase.zookeeper.quorum`
 *        [[http://hbase.apache.org/book/config.files.html configuration]] property.
 * @param tableName Event message table name.
 * @param name Optional journal actor name.
 * @param dispatcherName Optional journal actor dispatcher name.
 * @param writerCount Number of concurrent writers.
 * @param writeTimeout Timeout for asynchronous writes.
 * @param initTimeout Timeout for journal initialization. During initialization
 *        the highest stored sequence number is loaded from the event message table.
 * @param replayChunkSize Maximum number of event messages to keep in memory during replay.
 */
case class HBaseJournalProps(
  zookeeperQuorum: String,
  tableName: String = DefaultTableName,
  name: Option[String] = None,
  dispatcherName: Option[String] = None,
  writerCount: Int = 16,
  writeTimeout: FiniteDuration = 10 seconds,
  initTimeout: FiniteDuration = 30 seconds,
  replayChunkSize: Int = 16 * 100) extends JournalProps {

  def withTableName(tableName: String) =
    copy(tableName = tableName)

  def withName(name: String) =
    copy(name = Some(name))

  def withDispatcherName(dispatcherName: String) =
    copy(dispatcherName = Some(dispatcherName))

  def withWriterCount(writerCount: Int) =
    copy(writerCount = writerCount)

  def withWriteTimeout(writeTimeout: FiniteDuration) =
    copy(writeTimeout = writeTimeout)

  def withInitTimeout(initTimeout: FiniteDuration) =
    copy(initTimeout = initTimeout)

  def withReplayChunkSize(replayChunkSize: Int) =
    copy(replayChunkSize = replayChunkSize)

  def journal: Actor =
    new HBaseJournal(this)
}
