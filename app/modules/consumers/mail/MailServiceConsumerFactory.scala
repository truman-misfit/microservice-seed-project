package com.misfit.microservices.modules

import play.api.Logger
import collection.JavaConversions._
import com.amazonaws.services.kinesis.model.Record
import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason

class MailServiceConsumerFactory extends IRecordProcessorFactory {

	override def createProcessor: IRecordProcessor = {
		Logger.info("create processor")
		return new StreamProcessor()
	}

	class StreamProcessor extends IRecordProcessor {
		override def initialize(sharId: String) = {
			Logger.info("initialize stream processor for shard: " + sharId)
		}

		override def processRecords(
			records: java.util.List[Record], 
			checkpointer: IRecordProcessorCheckpointer) = {
			records.foreach(record => processRecord(record))
			checkpoint(checkpointer)
		}

		override def shutdown(
			checkpointer: IRecordProcessorCheckpointer,
			reason: ShutdownReason) = {
			if (ShutdownReason.TERMINATE == reason) {
				checkpoint(checkpointer)
			}
		}

		private def processRecord(record: Record) = {
			val data = new String(record.getData().array())
			println(data)
		}

		private def checkpoint(checkpointer: IRecordProcessorCheckpointer) = {
			try {
				checkpointer.checkpoint()
			} catch {
				case se: ShutdownException => {
					Logger.info("Caught shutdown exception, skipping checkpoint.", se)
				}
				case e: ThrottlingException => {
					Logger.error("Caught throttling exception, skipping checkpoint.", e)
				}
				case e: InvalidStateException => {
					Logger.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.", e)
				}
			}
		}
	}
}