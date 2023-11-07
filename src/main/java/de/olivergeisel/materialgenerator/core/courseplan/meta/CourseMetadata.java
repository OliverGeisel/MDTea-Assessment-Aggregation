package de.olivergeisel.materialgenerator.core.courseplan.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CourseMetadata {
	private final Map<String, String> otherInfos;
	private       Optional<String>    name;
	private       Optional<String>    year;
	private       Optional<String>    level;
	private       Optional<String>    type;
	private       Optional<String>    description;

	public CourseMetadata(String name, String year, String level, String type, String description,
			Map<String, String> rest) {
		this.name = Optional.of(name != null ? name : "");
		this.year = Optional.of(year != null ? year : "");
		this.level = Optional.of(level != null ? level : "");
		this.type = Optional.of(type != null ? type : "");
		this.description = Optional.of(description != null ? description : "");
		otherInfos = new HashMap<>();
		otherInfos.putAll(rest);
	}

	public static CourseMetadata emptyMetadata() {
		return new CourseMetadata("", "", "", "", "", Map.of());
	}

	public boolean addOtherInfo(String key, String value) {
		if (key == null || value == null) {
			return false;
		}
		return otherInfos.put(key, value) != null;
	}

	public boolean removeOtherInfo(String key) {
		if (key == null) {
			return false;
		}
		return otherInfos.remove(key) != null;
	}

	//region setter/getter
	public Optional<String> getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description == null) {
			throw new IllegalArgumentException("Description must not be null");
		}
		this.description = Optional.of(description);
	}

	public Optional<String> getLevel() {
		return level;
	}

	public void setLevel(String level) {
		if (level == null) {
			throw new IllegalArgumentException("Level must not be null");
		}
		this.level = Optional.of(level);
	}

	public Optional<String> getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name must not be null");
		}
		this.name = Optional.of(name);
	}

	public Map<String, String> getOtherInfos() {
		return otherInfos;
	}

	public Optional<String> getType() {
		return type;
	}

	public void setType(String type) {
		if (type == null) {
			throw new IllegalArgumentException("Type must not be null");
		}
		this.type = Optional.of(type);
	}

	public Optional<String> getYear() {
		return year;
	}

	public void setYear(String year) {
		if (year == null) {
			throw new IllegalArgumentException("Year must not be null");
		}
		this.year = Optional.of(year);
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CourseMetadata that)) return false;

		if (!otherInfos.equals(that.otherInfos)) return false;
		if (!Objects.equals(name, that.name)) return false;
		if (!Objects.equals(year, that.year)) return false;
		if (!Objects.equals(level, that.level)) return false;
		if (!Objects.equals(type, that.type)) return false;
		return Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		int result = otherInfos.hashCode();
		result = 31 * result + (name.isPresent() ? name.hashCode() : 0);
		result = 31 * result + (year.isPresent() ? year.hashCode() : 0);
		result = 31 * result + (level.isPresent() ? level.hashCode() : 0);
		result = 31 * result + (type.isPresent() ? type.hashCode() : 0);
		result = 31 * result + (description.isPresent() ? description.hashCode() : 0);
		return result;
	}
}
