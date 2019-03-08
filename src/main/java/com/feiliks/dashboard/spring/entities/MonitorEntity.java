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

	@Column(name = "exec_rate")
	private long execRate;

	@ManyToOne
	private DatabaseEntity database;

	@Column(name = "config_data")
	@Lob
	private String configData;

	@OneToMany(mappedBy = "monitor")
	private Collection<BlockEntity> blocks;

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

	public Collection<BlockEntity> getBlocks() {
		return blocks;
	}

	public void setBlocks(Collection<BlockEntity> blocks) {
		this.blocks = blocks;
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
			return super.equals(other);
		if (other instanceof MonitorEntity) {
			MonitorEntity entity = (MonitorEntity) other;
			return id.equals(entity.id);
		}
		return false;
	}

}

