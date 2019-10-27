package com.yc.listener;

import com.yc.server.Server.ClientCon;

public class Even {

	private ClientCon clientCon;

	public Even(ClientCon clientCon) {
		super();
		this.clientCon = clientCon;
	}

	public Even() {
		super();
	}

	public ClientCon getClientCon() {
		return clientCon;
	}

	public void setClientCon(ClientCon clientCon) {
		this.clientCon = clientCon;
	}
	
	
}
