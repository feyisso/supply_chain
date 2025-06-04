package com.awashbank.supply_chain.user.model;

import java.sql.Date;

public class sysResponse {
    private String system_name;
    private String channalId;
    private String system_image;
    private String system_home;
    private Date create_date;
    private String created_by;
    private Integer status;

    public sysResponse() {
    }

    public String getSystem_name() {
        return system_name;
    }

    public void setSystem_name(String system_name) {
        this.system_name = system_name;
    }

    public String getChannalId() {
        return channalId;
    }

    public void setChannalId(String channalId) {
        this.channalId = channalId;
    }

    public String getSystem_image() {
        return system_image;
    }

    public void setSystem_image(String system_image) {
        this.system_image = system_image;
    }

    public String getSystem_home() {
        return system_home;
    }

    public void setSystem_home(String system_home) {
        this.system_home = system_home;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
