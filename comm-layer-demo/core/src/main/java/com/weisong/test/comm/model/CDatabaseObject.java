package com.weisong.test.comm.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
abstract public class CDatabaseObject extends CObject {

	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;

}
