#!/usr/bin/python

import mysql.connector

conn = mysql.connector.connect(user='root', password='12330321', database='test', use_unicode=True)
cursor = conn.cursor()

cursor.execute('create table users (name varchar(20) binary primary key, password varchar(20) binary)')
cursor.execute('create table plans (user varchar(20) binary, date varchar(20) binary, content varchar(100) binary)')
conn.commit()

cursor.close()
conn.close()