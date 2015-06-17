#!/usr/bin/python

import time, threading
import mysql.connector
import zmq
import global_list

conn = mysql.connector.connect(user='root', password='12330321', database='test', use_unicode=True)
cursor = conn.cursor()

context = zmq.Context()
sock = context.socket(zmq.REP)
sock.bind("tcp://*:8002")

def SignIn(sock, data_info):
	cursor.execute('select password from users where binary name = %s', [data_info[1]])
	ps = cursor.fetchone()
	if ps:
		if ps[0] == data_info[2]:
			sock.send('LIS')
		else:
			sock.send('WP')
	else:
		sock.send('UDE')

def SignUp(sock, data_info):
	cursor.execute('select * from users where binary name = %s limit 1', [data_info[1]])
	cursor.fetchone()
	if cursor.rowcount == 1:
		sock.send('UHE')
	else:
		cursor.execute('insert into users (name, password) values (%s, %s)', [data_info[1], data_info[2]])
		conn.commit()
		sock.send('SS')

def Load(sock, data_info):
	num = (int)(data_info[1])
	msg = ""
	if global_list.flag == True:
		cursor.execute('select * from plans')
		data = cursor.fetchall()
		size = len(data)
		if size == 0:
			sock.send('LF')
			return
		if num > size:
			n = size
		else:
			n = num
		j = 0
		while global_list.mc.get(str(j)) != None:
			global_list.mc.delete(str(j))
			j += 1
		for i in range(size-1, size-1-n, -1):
			line = data[i][0] + '&' + data[i][1] + '&' + data[i][2] + '|'
			global_list.mc.set(str(size-1-i), line)
			msg += line
		global_list.flag = False
	else:
		i = 0
		while global_list.mc.get(str(i)) != None:
			info = str(global_list.mc.get(str(i)))
			msg += info
			i += 1
	print msg
	sock.send(msg)

def Store(sock, data_info):
	cursor.execute('insert into plans (user, date, content) values (%s, %s, %s)', [data_info[1], data_info[2], data_info[3]])
	conn.commit()
	sock.send('SS')
	global_list.flag = True


print 'Waiting for connection...'

while True:
	data = sock.recv()
	time.sleep(1)
	if not data or data == 'exit':
		sock.send("Disconnect")

	print "Got", data

	data_info = data.split('&')
	if data_info[0] == 'SignIn':
		SignIn(sock, data_info)
	elif data_info[0] == 'SignUp':
		SignUp(sock, data_info)
	elif data_info[0] == 'Load':
		Load(sock, data_info)
	elif data_info[0] == 'Store':
		Store(sock, data_info)
	else:
		sock.send("WM")

sock.close()
