package com.test.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

import com.entity.TestEntityClass;

public class RmiServiceImpl implements RmiService, Serializable{

	private static final long serialVersionUID = -1584530783212530104L;

	public TestEntityClass getObject() throws RemoteException{
		TestEntityClass test = new TestEntityClass();
		test.setId("1");
		test.setName("rmiName");
		return test;
	}
}
