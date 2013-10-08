package com.github.ldriscoll.ektorplucene.designdocument;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Defaults;

/**
 * Class representing the "defaults" attribute of an index.
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class LuceneDefaults {

    /**
     * Create a {@link LuceneDefaults} from a {@link Defaults} annotation.
     * Returns null if no values are set.
     */
    public static LuceneDefaults fromAnnotation(Defaults defaults) {
        LuceneDefaults lid = new LuceneDefaults();
        boolean hasValues = false;

        if (!StringUtils.isBlank(defaults.field())) {
            lid.setField(defaults.field());
            hasValues = true;
        }
        if (!StringUtils.isBlank(defaults.type())) {
            lid.setType(defaults.type());
            hasValues = true;
        }
        if (!StringUtils.isBlank(defaults.store())) {
            lid.setStore(defaults.store());
            hasValues = true;
        }
        if (!StringUtils.isBlank(defaults.index())) {
            lid.setIndex(defaults.index());
            hasValues = true;
        }
        return hasValues ? lid : null;
    }

    @JsonProperty
    private String field;
    @JsonProperty
    private String type;
    @JsonProperty
    private String store;
    @JsonProperty
    private String index;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((store == null) ? 0 : store.hashCode());
        result = prime * result + ((index == null) ? 0 : index.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        LuceneDefaults other = (LuceneDefaults) obj;
        if (field == null) {
            if (other.field != null)
                return false;
        } else if (!field.equals(other.field))
            return false;

        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;

        if (store == null) {
            if (other.store != null)
                return false;
        } else if (!store.equals(other.store))
            return false;

        if (index == null) {
            if (other.index != null)
                return false;
        } else if (!index.equals(other.index))
            return false;

        return true;
    }

}
