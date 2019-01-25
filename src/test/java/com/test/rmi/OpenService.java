package com.test.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class OpenService {
	static RmiService rmiService = new RmiServiceImpl();
	
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(8081);
			Naming.rebind("rmi://127.0.0.1:8081/RmiService", rmiService);
		} catch (RemoteException e) {
			System.err.println("open port failure!");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.err.println("bind error!");
			e.printStackTrace();
		}
		System.out.println("begin service");
	}
}
