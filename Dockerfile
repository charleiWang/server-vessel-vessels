FROM redmic/redmic-server

ENV SERVICE=undefined-service

COPY /*/dist/*.jar ./

EXPOSE ${COMMANDS_PORT} ${VIEW_PORT}

ENTRYPOINT java ${JAVA_OPTS} \
	-Djava.security.egd=file:/dev/./urandom \
	-jar ${DIRPATH}/${SERVICE}.jar
