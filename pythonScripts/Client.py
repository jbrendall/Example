"""
TraceDynamo python extension client
"""

import argparse
import socket
import sys
import struct
import json

import logging

###############################################################################
# Python extensions
###############################################################################

import lda
import lsi
#import link_completion

###############################################################################
# Logging
###############################################################################

def setup_logging(path):
    """Setup logging to console and file

    Returns the root logger
    """
    fmt = '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    logging.basicConfig(level=logging.DEBUG, format=fmt)
    formatter = logging.Formatter(fmt)

    filename = path+'PyExtClient.log'

    file_handler = logging.FileHandler(filename)
    file_handler.setLevel(logging.DEBUG)
    file_handler.setFormatter(formatter)
    logging.getLogger().addHandler(file_handler)
    return logging.getLogger(__name__)


###############################################################################
# Communication
###############################################################################

def send_json_message(sock, json_data):
    """Send json encoded 'json_data' using 'sock'
    """
    msg_bytes = json_data.encode('UTF-8')
    l = len(msg_bytes)
    # send header
    sock.send(struct.pack('!I', l))
    # send content
    sock.send(msg_bytes)


def send_message(sock, data):
    """Send message using 'sock' and encoding 'data' as JSON
    """


    json_data = json.dumps(data, indent=4).encode('UTF-8')
    send_json_message(sock, json_data)


def receive_message(sock, LOGGER):
    """receive message from 'sock' and returning JSON data
    """

    header_bytes = sock.recv(4)
    msg_len = struct.unpack('!I', header_bytes)[0]
    LOGGER.debug('expect message len = %d', msg_len)


    message = ""
    while "\n" not in message:
    	message = message + sock.recv(4096).decode('utf-8')
    return json.loads(message)


def dispatch(input_data: dict, LOGGER) -> dict:
    """Dispatch JAVA input data

    Expected input format
    {
        'name': -> extension name
        'args': -> extension arguments
        'data': -> extension data
    }

    Returns result of algorithm call
    """

    algorithm = input_data['name']
    args = input_data['args']
    data = input_data['data']

    if algorithm == 'LDA':
        LOGGER.info('call lda with %s', args)
        result = lda.run(args, data)
        return result
        return json.dumps({'lda_result': data})

    elif algorithm == 'LSI':
        LOGGER.info('call lsi with %s', args)
        result = lsi.run(args, data)
        return result
        return json.dumps({'lsi_result': data})

    elif algorithm == 'LC':
        result = link_completion.run(algorithm_arguments=args,
                                     project_data=data)
        return result
    else:
        raise Exception('Unknown algorithm "{}"'.format(algorithm))


def main():
    """Main communication function
    """
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', help='port number for socket connection',
                        type=int, default=4444)
    parser.add_argument('--path', help='directory for log file save location',
    					type=str, default='')

    args = parser.parse_args()

    LOGGER = setup_logging(args.path)

    LOGGER.info('*' * 40)
    LOGGER.info('TraceDynamo Python extension, stdout/stderr encoding is "%s"',
                sys.stdout.encoding)


    LOGGER.debug('received port: %d', args.port)

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(('localhost', args.port))

    input_data = receive_message(sock, LOGGER)
    result_data = dispatch(input_data, LOGGER)

    send_json_message(sock, result_data)

    sock.shutdown(socket.SHUT_RDWR)
    sock.close()

###############################################################################
# Script entry point
###############################################################################

if __name__ == '__main__':
    main()
    sys.exit(0)
