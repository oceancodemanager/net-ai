package com.ocean.net.test.db;

public class TxEntity implements java.io.Serializable {
	@Override
	public String toString() {
		return "TxEntity [id=" + id + ", name=" + name + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String name;
}
