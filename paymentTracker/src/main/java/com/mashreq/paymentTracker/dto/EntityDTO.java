package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

import com.mashreq.paymentTracker.type.EntityType;

public class EntityDTO implements Serializable{
	
	private static final long serialVersionUID = -4303571985235664273L;
	private Long id;
	private String entityName;
	private String sourceFormat;
	private String displayFormat;
	private EntityType entityType;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getSourceFormat() {
		return sourceFormat;
	}
	public void setSourceFormat(String sourceFormat) {
		this.sourceFormat = sourceFormat;
	}
	public String getDisplayFormat() {
		return displayFormat;
	}
	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}
	public EntityType getEntityType() {
		return entityType;
	}
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
	@Override
	public String toString() {
		return "EntityDTO [id=" + id + ", entityName=" + entityName + ", sourceFormat=" + sourceFormat
				+ ", displayFormat=" + displayFormat + ", entityType=" + entityType + "]";
	}
	
	
}
