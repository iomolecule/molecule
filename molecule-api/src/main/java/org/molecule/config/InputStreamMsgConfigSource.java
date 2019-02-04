package org.molecule.config;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class InputStreamMsgConfigSource extends InputStreamConfigurationSource implements MsgConfigSource{


    public InputStreamMsgConfigSource(boolean allowNullStreams, boolean throwErrorOnMismatchedTypes, InputStream... inputStreams) {
        super(allowNullStreams, throwErrorOnMismatchedTypes, inputStreams);
    }
}
