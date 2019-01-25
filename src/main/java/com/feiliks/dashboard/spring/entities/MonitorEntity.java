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

	@Column(name = "exec_rate", nullable = false)
	private long execRate;

	@Column(name = "config_data")
	private String configData;

	@ManyToOne
	private DatabaseEntity database;

	@OneToMany(mappedBy = "monitor", cascade = CascadeType.ALL)
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

	public String getConfigData() {
		return configData;
	}

	public void setConfigData(String configData) {
		this.configData = configData;
	}

	public long getExecRate() {
		return execRate;
	}

	public void setExecRate(long execRate) {
		this.execRate = execRate;
	}

	public DatabaseEntity getDatabase() {
		return database;
	}

	public void setDatabase(DatabaseEntity database) {
		this.database = database;
	}

	public Collection<DataSourceEntity> getDataSources() {
		return dataSources;
	}

	public void setDataSources(Collection<DataSourceEntity> dataSources) {
		this.dataSources = dataSources;
	}

	@Override
	public int hashCode() {
		if (id == null)
			return 0;
		return id.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (id == null)
			return this == other;
		if (other instanceof MonitorEntity) {
			MonitorEntity entity = (MonitorEntity) other;
			return id.equals(entity.id);
		}
		return false;
	}

}

