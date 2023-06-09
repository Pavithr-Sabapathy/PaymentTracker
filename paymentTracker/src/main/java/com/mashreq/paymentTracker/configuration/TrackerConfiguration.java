package com.mashreq.paymentTracker.configuration;

import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:tracker.properties")
public class TrackerConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "threadpool")
	public ThreadPoolConfig getThreadPoolConfig() {

		return new ThreadPoolConfig();
	}

	@Bean
	@ConfigurationProperties(prefix = "sourcepool")
	public SourcePoolConfig getSourcePoolConfig() {

		return new SourcePoolConfig();
	}

	@Bean
	@ConfigurationProperties(prefix = "exportfile")
	public ExportFileConfig getExportFileConfig() {

		return new ExportFileConfig();
	}

	@Bean
	@ConfigurationProperties(prefix = "querykey")
	public QueryKeyConfig getQueryKeyConfig() {
		return new QueryKeyConfig();
	}

	@Bean
	@ConfigurationProperties(prefix = "regex")
	public RegexConfig getPromptKeyConfig() {
		return new RegexConfig();
	}

	public static class SourcePoolConfig {

		private Boolean testOnBorrow;
		private Integer maxActive;
		private Integer maxIdle;

		public void setTestOnBorrow(Boolean testOnBorrow) {
			this.testOnBorrow = testOnBorrow;
		}

		public void setMaxActive(Integer maxActive) {
			this.maxActive = maxActive;
		}

		public void setMaxIdle(Integer maxIdle) {
			this.maxIdle = maxIdle;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("---------------- Source Pool Config Values ------------------ ").append("\n");
			sb.append(ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
			return sb.toString();
		}

	}

	public static class ThreadPoolConfig {

		private Integer size;
		private Integer maxSize;
		private Integer queueCapacity;
		private String threadPrefix;

		public void setSize(Integer size) {
			this.size = size;
		}

		public void setMaxSize(Integer maxSize) {
			this.maxSize = maxSize;
		}

		public void setQueueCapacity(Integer queueCapacity) {
			this.queueCapacity = queueCapacity;
		}

		public void setThreadPrefix(String threadPrefix) {
			this.threadPrefix = threadPrefix;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("---------------- Thread Pool Config Values ------------------ ").append("\n");
			sb.append(ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
			return sb.toString();
		}
	}

	public static class ExportFileConfig {

		private String storageFilePath;
		private Integer fileNameLength;

		public void setStorageFilePath(String storageFilePath) {
			this.storageFilePath = storageFilePath;
		}

		public void setFileNameLength(Integer fileNameLength) {
			this.fileNameLength = fileNameLength;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("---------------- Export File Config Values ------------------ ").append("\n");
			sb.append(ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
			return sb.toString();
		}
	}

	public static class QueryKeyConfig {

		private String referral_ConfigFileName;
		private String wrmRmCreditOps_ConfigFileName;
		private String dataSourceNames_ConfigFileName;

		public void setReferral_ConfigFileName(String referral_ConfigFileName) {
			this.referral_ConfigFileName = referral_ConfigFileName;
		}

		public void setWrmRmCreditOps_ConfigFileName(String wrmRmCreditOps_ConfigFileName) {
			this.wrmRmCreditOps_ConfigFileName = wrmRmCreditOps_ConfigFileName;
		}

		public void setDataSourceNames_ConfigFileName(String dataSourceNames_ConfigFileName) {
			this.dataSourceNames_ConfigFileName = dataSourceNames_ConfigFileName;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("---------------- Query Key Config Values ------------------ ").append("\n");
			sb.append(ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
			return sb.toString();
		}

	}

	public static class RegexConfig {

		private String promptKey;
		private Pattern promptKeyPattern;

		public void setPromptKey(String promptKey) {
			this.promptKey = promptKey;
			this.promptKeyPattern = Pattern.compile(promptKey);
		}

		public Pattern getPromptKeyPattern() {
			return promptKeyPattern;
		}

		@Override
		public String toString() {
			return "RegexConfig [promptKey=" + promptKey + "]";
		}

	}

	public Boolean getTestOnBorrow() {
		return getSourcePoolConfig().testOnBorrow;
	}

	public Integer getMaxActive() {
		return getSourcePoolConfig().maxActive;
	}

	public Integer getMaxIdle() {
		return getSourcePoolConfig().maxIdle;
	}

	public Integer getMaxSize() {
		return getThreadPoolConfig().maxSize;
	}

	public Integer getSize() {
		return getThreadPoolConfig().size;
	}

	public Integer getQueueCapacity() {
		return getThreadPoolConfig().queueCapacity;
	}

	public String getThreadPrefix() {
		return getThreadPoolConfig().threadPrefix;
	}

	public String getStorageFilePath() {
		return getExportFileConfig().storageFilePath;
	}

	public Integer getFileNameLength() {
		return getExportFileConfig().fileNameLength;
	}

	public String getReferral_ConfigFileName() {
		return getQueryKeyConfig().referral_ConfigFileName;
	}

	public String getWrmRmCreditOps_ConfigFileName() {
		return getQueryKeyConfig().wrmRmCreditOps_ConfigFileName;
	}

	public String getDataSourceNames_ConfigFileName() {
		return getQueryKeyConfig().dataSourceNames_ConfigFileName;
	}

	public String getPromptKey() {
		return getPromptKeyConfig().promptKey;
	}

}
