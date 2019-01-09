package com.feiliks.dashboard.spring.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "dashboard_dashboard")
public class DashboardEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "path_key", nullable = false, unique = true)
	private String pathKey;

	@Column(nullable = false, length = 32)
	private String name;

	@Column(name = "is_active", nullable = false)
	private boolean active;

	@ManyToOne(optional = false)
	private TemplateEntity template;

	@OneToMany(mappedBy = "dashboard")
	private Collection<BlockEntity> blocks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPathKey() {
		return pathKey;
	}

	public void setPathKey(String pathKey) {
		this.pathKey = pathKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public TemplateEntity getTemplate() {
		return template;
	}

	public void setTemplate(TemplateEntity template) {
		this.template = template;
	}

	public Collection<BlockEntity> getBlocks() {
		return blocks;
	}

	public void setBlocks(Collection<BlockEntity> blocks) {
		this.blocks = blocks;
	}

}

