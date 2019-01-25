package com.test.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import com.entity.TestEntityClass;

interface RmiService extends Remote{
	TestEntityClass getObject() throws RemoteException;
}
