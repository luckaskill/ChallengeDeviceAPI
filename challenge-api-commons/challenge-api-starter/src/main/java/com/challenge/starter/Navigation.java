package com.challenge.starter;

public interface Navigation {

	//	Parameters
	String ID = "id";
	String ID_PATH_PARAM = "{" + ID + "}";

	//	Controllers
	String DEVICE = "/device";

	//	Endpoints
	String BY_ID = "/" + ID_PATH_PARAM;
}
