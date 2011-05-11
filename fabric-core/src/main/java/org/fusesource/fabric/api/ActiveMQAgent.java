/**
 * Copyright (C) 2011, FuseSource Corp.  All rights reserved.
 * http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */
package org.fusesource.fabric.api;

import org.fusesource.fabric.api.data.QueueInfo;
import org.fusesource.fabric.api.data.TopicInfo;

/**
 * An activemq agent interface.
 *
 * @author ldywicki
 */
public interface ActiveMQAgent {

    TopicInfo[] getTopics();
    QueueInfo[] getQueues();

}
