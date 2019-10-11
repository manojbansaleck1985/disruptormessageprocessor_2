package com.abc.poc.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.poc.messaging.vo.TaskEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;

/**
 * 
 * @author Manoj Bansal
 * 
 * This event handler will check the id of the producer and redirect the TaskEvent to either abDisruptor or cdDisruptor.
 *
 */
public class DispatcherEventHandler implements EventHandler<TaskEvent> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherEventHandler.class);

	private RingBuffer<TaskEvent> abRingBuffer;
	
	private RingBuffer<TaskEvent> cdRingBuffer;
	
	public DispatcherEventHandler(RingBuffer<TaskEvent> abRingBuffer, RingBuffer<TaskEvent> cdRingBuffer) {
		this.abRingBuffer = abRingBuffer;
		this.cdRingBuffer = cdRingBuffer;
	}
	
	@Override
	public void onEvent(TaskEvent taskEvent, long seq, boolean arg2) throws Exception {
		if(taskEvent.getValue().startsWith("A") || taskEvent.getValue().startsWith("B")) {
			LOGGER.debug("{} Redirecting task event {} to abDisruptor for processing.", seq , taskEvent);
			long seq1 = abRingBuffer.next();
			TaskEvent abTaskEvent = abRingBuffer.get(seq1);
			abTaskEvent.setValue(taskEvent.getValue());
			abRingBuffer.publish(seq1);
		}
		if(taskEvent.getValue().startsWith("C") || taskEvent.getValue().startsWith("D")) {
			LOGGER.debug("{} Redirecting task event {} to cdDisruptor for processing.", seq,taskEvent);
			long seq1 = cdRingBuffer.next();
			TaskEvent cdTaskEvent = cdRingBuffer.get(seq1);
			cdTaskEvent.setValue(taskEvent.getValue());
			cdRingBuffer.publish(seq1);
		}
		
	}
	

}
