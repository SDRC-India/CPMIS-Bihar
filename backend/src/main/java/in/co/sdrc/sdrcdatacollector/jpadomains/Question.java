package in.co.sdrc.sdrcdatacollector.jpadomains;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

import in.co.sdrc.sdrcdatacollector.util.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author azaruddin
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class)
@ToString
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"columnName", "columnTableName"}))
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer slugId;

	private Integer formId;

	private Integer questionId;

	@Column(length = 400)
	private String section;

	@Column(length = 400)
	private String subsection;

	@Column(length = 400)
	private String question;

	private String columnName;
	
	private String columnTableName;

	private String controllerType;

	private String parentColumnName;

	private String fieldType;

	@JoinColumn(name = "type_id_fk")
	@ManyToOne
	private Type typeId;

	private String repeatSubSection;

	private Integer questionOrder;

	@Column(length = 2000)
	private String relevance;
	@Column(length = 3000)
	private String constraints;

	@Column(length = 3000)
	private String features;

	private String defaultSettings;

	private String tableName;

	private String defaultValue;

	private Boolean active = true;

	private Boolean mask;

	private Boolean saveMandatory = false;

	private Boolean finalizeMandatory = false;

	private Boolean displayScore = false;

	private String query;

	private String reviewHeader;

	private String fileExtensions;

	private String scoreExp;

	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;

	@org.hibernate.annotations.Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb default null")
	private JsonNode extraKeys;

	@org.hibernate.annotations.Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb default null")
	private JsonNode languages;
	
	@Column(length = 2000)
	private String cmsg;

}
