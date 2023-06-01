CREATE TABLE [dbo].[RAPPE](
	[aid] [varchar](255) NULL,
	[appe_s_umidl] [varchar](255) NULL,
	[appe_s_umidh] [varchar](255) NULL,
	[appe_inst_num] [varchar](255) NULL,
	[appe_date_time] [datetime] NULL,
	[appe_seq_nbr] [varchar](255) NULL,
	[appe_iapp_name] [varchar](255) NULL,
	[appe_type] [varchar](255) NULL,
	[appe_session_holder] [varchar](255) NULL,
	[appe_session_nbr] [varchar](255) NULL,
	[appe_sequence_nbr] [varchar](255) NULL,
	[appe_transmission_nbr] [varchar](255) NULL,
	[appe_crea_appl_serv_name] [varchar](255) NULL,
	[appe_crea_mpfn_name] [varchar](255) NULL,
	[appe_crea_rp_name] [varchar](255) NULL,
	[appe_data_last] [varchar](255) NULL,
	[appe_token] [varchar](255) NULL,
	[appe_ack_nack_text] [varchar](255) NULL,
	[appe_answerback] [varchar](255) NULL,
	[appe_auth_result] [varchar](255) NULL,
	[appe_auth_value] [varchar](255) NULL,
	[appe_carrier_acceptance_id] [varchar](255) NULL,
	[appe_checksum_result] [varchar](255) NULL,
	[appe_checksum_value] [varchar](255) NULL,
	[appe_conn_response_code] [varchar](255) NULL,
	[appe_conn_response_text] [varchar](255) NULL,
	[appe_crest_com_server_id] [varchar](255) NULL,
	[appe_crest_gateway_id] [varchar](255) NULL,
	[appe_cui] [varchar](255) NULL,
	[appe_fax_batch_sequence] [varchar](255) NULL,
	[appe_fax_duration] [varchar](255) NULL,
	[appe_fax_number] [varchar](255) NULL,
	[appe_fax_tnap_name] [varchar](255) NULL,
	[appe_local_output_time] [time](7) NULL,
	[appe_nak_reason] [varchar](255) NULL,
	[appe_network_delivery_status] [varchar](255) NULL,
	[appe_pac_result] [varchar](255) NULL,
	[appe_pac_value] [varchar](255) NULL,
	[appe_rcv_delivery_status] [varchar](255) NULL,
	[appe_remote_input_reference] [varchar](255) NULL,
	[appe_remote_input_time] [time](7) NULL,
	[appe_sender_cancel_status] [varchar](255) NULL,
	[appe_telex_batch_sequence] [varchar](255) NULL,
	[appe_telex_duration] [varchar](255) NULL,
	[appe_telex_number] [varchar](255) NULL,
	[appe_tnap_name] [varchar](255) NULL,
	[appe_sender_swift_address] [varchar](255) NULL,
	[x_appe_last] [varchar](255) NULL,
	[appe_ack_nack_lau_result] [varchar](255) NULL,
	[appe_combined_auth_res] [varchar](255) NULL,
	[appe_combined_pac_res] [varchar](255) NULL,
	[appe_lau_result] [varchar](255) NULL,
	[appe_pki_auth_result] [varchar](255) NULL,
	[appe_pki_authentication_res] [varchar](255) NULL,
	[appe_pki_authorisation_res] [varchar](255) NULL,
	[appe_pki_pac2_result] [varchar](255) NULL,
	[appe_rma_check_result] [varchar](255) NULL,
	[appe_use_pki_signature] [varchar](255) NULL,
	[appe_swift_ref] [varchar](255) NULL,
	[appe_swift_request_ref] [varchar](255) NULL,
	[appe_nr_indicator] [varchar](255) NULL,
	[appe_nonrep_type] [varchar](255) NULL,
	[appe_nonrep_warning] [varchar](255) NULL,
	[appe_authoriser_dn] [varchar](255) NULL,
	[appe_signer_dn] [varchar](255) NULL,
	[appe_snl_endpoint] [varchar](255) NULL,
	[appe_snf_queue_name] [varchar](255) NULL,
	[appe_snf_input_time] [varchar](255) NULL,
	[appe_snf_delv_notif_req] [varchar](255) NULL,
	[appe_swift_response_ref] [varchar](255) NULL,
	[appe_response_ref] [varchar](255) NULL,
	[appe_resp_nonrep_type] [varchar](255) NULL,
	[appe_resp_nonrep_warning] [varchar](255) NULL,
	[appe_resp_cbt_reference] [varchar](255) NULL,
	[appe_resp_possible_dup_crea] [varchar](255) NULL,
	[appe_resp_responder_dn] [varchar](255) NULL,
	[appe_resp_auth_result] [varchar](255) NULL,
	[appe_resp_signer_dn] [varchar](255) NULL,
	[appe_large_data] [varchar](255) NULL,
	[appe_record_id] [varchar](255) NULL,
	[APPE_MESG_INPUT_REFERENCE] [varchar](255) NULL,
	[APPE_TRANSLATION_DIGEST_RESULT] [varchar](255) NULL,
	[APPE_CLIENT_REF] [varchar](255) NULL,
	[APPE_COMBINED_AUTHENTICATION_R] [varchar](255) NULL,
	[APPE_RESP_PAYLOAD_ATTRIBUTE_NA] [varchar](255) NULL,
	[APPE_RESP_PAYLOAD_ATTRIBUTE_VA] [varchar](255) NULL,
	[APPE_TRANSFER_REF] [varchar](255) NULL,
	[APPE_STORED_TRANSFER_REF] [varchar](255) NULL,
	[APPE_ORIG_SNF_TRANSFER_REF] [varchar](255) NULL,
	[APPE_AUTH_DELV_NOTIF_REQ] [varchar](255) NULL,
	[APPE_DELV_NOTIF_REQ_RECDN] [varchar](255) NULL,
	[APPE_DELV_NOTIF_REQ_MTYPE] [varchar](255) NULL,
	[APPE_DELIVERY_TIME] [time](7) NULL,
	[APPE_CREATION_TIME] [time](7) NULL,
	[APPE_FILE_START_TIME] [time](7) NULL,
	[APPE_FILE_END_TIME] [time](7) NULL,
	[APPE_THIRD_PARTY_SIGNER_DN] [varchar](255) NULL,
	[APPE_COPIED_THIRD_PARTY_LIST] [varchar](255) NULL,
	[APPE_SKIPPED_THIRD_PARTY_LIST] [varchar](255) NULL,
	[APPE_SENDER_BIC8] [varchar](255) NULL,
	[APPE_XMLV2_PHYSICAL_FILENAME] [varchar](255) NULL,
	[APPE_PAYLOAD_PHYSICAL_FILENAME] [varchar](255) NULL,
	[APPE_RECEPT_ACK_NACK_DATE_TIME] [varchar](255) NULL,
	[APPE_RELEASE_INFO] [varchar](255) NULL,
	[APPE_CORRELATION_ID] [varchar](255) NULL,
	[APPE_PDE_ADDED_BY_SAA] [varchar](255) NULL,
	[APPE_SNF_CHANNEL_NAME] [varchar](255) NULL,
	[APPE_PSEUDO_APPENDIX] [varchar](255) NULL,
	[APPE_RMA_CHECK_SERVICE] [varchar](255) NULL,
	[APPE_MVAL_RESULT] [varchar](255) NULL,
	[APPE_RESP_MVAL_RESULT] [varchar](255) NULL,
	[APPE_RESP_AUTH_VALUE] [varchar](255) NULL,
	[APPE_PDM_HISTORY] [varchar](255) NULL,
	[APPE_PKI_AUTH_VALUE] [varchar](255) NULL,
	[APPE_PKI_PAC2_VALUE] [varchar](255) NULL,
	[APPE_TRANSLATION_RESULT_DETAIL] [varchar](255) NULL,
	[APPE_COPY_INFO] [varchar](255) NULL,
	[X_DS] [varchar](255) NULL,
	[X_CREA_DATE_TIME_MESG] [timestamp] NULL
) ON [PRIMARY]
GO
