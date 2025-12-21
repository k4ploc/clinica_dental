package com.clinica.errors;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String resourceName;
	private final String fieldName;
	private final Object fieldValue;

	public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public ResourceNotFoundException(String resourceName, Object id) {
		super(String.format("%s no encontrado con ID: %s", resourceName, id));
		this.resourceName = resourceName;
		this.fieldName = "ID";
		this.fieldValue = id;
	}

	public ResourceNotFoundException(String message) {
		super(message);
		this.resourceName = null;
		this.fieldName = null;
		this.fieldValue = null;
	}

	public String getResourceName() {
		return resourceName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}
}

