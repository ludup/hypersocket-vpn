package com.hypersocket.applications;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.hypersocket.resource.AssignableResource;

@Entity
@Table(name="applications")
public class ApplicationResource extends AssignableResource {

	/**
	 * TODO put your entities table name in the annotation above. Add
	 * any further fields your resource requires.
	 */
}
