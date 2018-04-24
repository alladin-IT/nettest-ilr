GCC_PARAMS = -Wall -Wno-unused-value -o rmbtd cwebsocket/base64_enc.c cwebsocket/sha1.c cwebsocket/websocket.c rmbtd.c -pthread -lrt -lssl -lcrypto
SERVER_DEP = cwebsocket/websocket.c rmbtd.c config.h secret.h

all: rmbtd

rmbtd: ${SERVER_DEP}
	gcc -O0 -g ${GCC_PARAMS}

server-prod: ${SERVER_DEP}
	gcc -O3 ${GCC_PARAMS}
	
clean:
	rm rmbtd
	rm server.key
	rm server.crt
	rm random

run: random cert rmbtd
	./rmbtd -l 8081 -L 8082 -c server.crt -k server.key -D

run-websocket: random cert rmbtd
	./rmbtd -w -l 8083 -L 8084 -c server.crt -k server.key -D

cert:
	(test -s server.key && test -s server.crt) || \
		openssl req -subj "/C=AT/ST=Vienna/L=Vienna/O=Example Company/CN=example.com" \
    	-newkey rsa:2048 -nodes -keyout server.key \
      -x509 -days 365 -out server.crt

random: 
	dd if=/dev/urandom of=random bs=1M count=100
