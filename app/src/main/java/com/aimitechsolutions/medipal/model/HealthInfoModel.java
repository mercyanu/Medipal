package com.aimitechsolutions.medipal.model;

public class HealthInfoModel {

        private String date;

        private String weight;

        private String height;

        private String bGroup;

        private String bPressure;

        private String bSugar;

        private String testTime;

        private String genotype;

        private String cholesterol;
        private String allergies;

    public HealthInfoModel(String date, String weight, String height, String bGroup, String bPressure, String bSugar, String testTime, String genotype, String cholesterol, String allergies) {
        this.date = date;
        this.weight = weight;
        this.height = height;
        this.bGroup = bGroup;
        this.bPressure = bPressure;
        this.bSugar = bSugar;
        this.testTime = testTime;
        this.genotype = genotype;
        this.cholesterol = cholesterol;
        this.allergies = allergies;
    }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getBGroup() {
            return bGroup;
        }

        public void setBGroup(String bGroup) {
            this.bGroup = bGroup;
        }

        public String getBPressure() {
            return bPressure;
        }

        public void setBPressure(String bPressure) {
            this.bPressure = bPressure;
        }

        public String getBSugar() {
            return bSugar;
        }

        public void setBSugar(String bSugar) {
            this.bSugar = bSugar;
        }

        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public String getGenotype() {
            return genotype;
        }

        public void setGenotype(String genotype) {
            this.genotype = genotype;
        }

        public String getCholesterol() {
            return cholesterol;
        }

        public void setCholesterol(String cholesterol) {
            this.cholesterol = cholesterol;
        }

        public String getAllergies() {
            return cholesterol;
        }

        public void setAllergies(String cholesterol) {
            this.cholesterol = cholesterol;
        }

}
