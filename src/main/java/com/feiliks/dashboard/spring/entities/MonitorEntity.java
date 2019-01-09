package com.feiliks.dashboard.spring.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "dashboard_monitor")
public class MonitorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 32)
	private String name;

	@Column(name = "java_class", nullable = false)
	private String javaClass;

	private String args;

	@Column(name = "exec_rate", nullable = false)
	private long execRate;

	@OneToMany(mappedBy = "monitor")
	private Collection<DataSourceEntity> dataSources;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public long getExecRate() {
		return execRate;
	}

	public void setExecRate(long execRate) {
		this.execRate = execRate;
	}

	public Collection<DataSourceEntity> getDataSources() {
		return dataSources;
	}

	public void setDataSources(Collection<DataSourceEntity> dataSources) {
		this.dataSources = dataSources;
	}
}

