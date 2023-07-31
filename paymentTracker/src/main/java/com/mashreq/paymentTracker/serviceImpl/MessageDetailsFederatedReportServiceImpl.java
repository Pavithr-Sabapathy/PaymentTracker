package com.mashreq.paymentTracker.serviceImpl;

public class MessageDetailsFederatedReportServiceImpl implements MessageDetailsFederatedReportService {
	/*
	 * 
	 * private static final Logger log =
	 * LoggerFactory.getLogger(MessageDetailsFederatedReportServiceImpl.class);
	 * private static final String FILENAME =
	 * "MessageDetailsFederatedReportServiceImpl";
	 * 
	 * @Autowired SwiftDetailedReportService swiftDetailedReportService;
	 * 
	 * @Autowired UAEFTSReportService UAEFTSReportService;
	 * 
	 * @Autowired ComponentsRepository componentRepository;
	 * 
	 * @Autowired ReportConfigurationService reportConfigurationService;
	 * 
	 * @Autowired CannedReportService cannedReportService;
	 * 
	 * @Autowired LinkReportService linkReportService;
	 * 
	 * @Autowired QueryExecutorService queryExecutorService;
	 * 
	 * public ReportExecuteResponseData processMessageDetailReport(ReportInstanceDTO
	 * reportInstanceDTO, ReportContext reportContext) { ReportExecuteResponseData
	 * responseData = new ReportExecuteResponseData();
	 * List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList =
	 * null; List<Map<String, Object>> messageData = null;
	 *//** fetch the report details based on report name **/
	/*
	 * Report report =
	 * reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName(
	 * )); CannedReport cannedReport =
	 * cannedReportService.populateCannedReportInstance(report);
	 * Optional<List<Components>> componentsOptional =
	 * componentRepository.findAllByreportId(cannedReport.getId());
	 * FederatedReportQueryData federatedReportQueryData = new
	 * FederatedReportQueryData();
	 * federatedReportQueryData.setCannedReportInstanceId(reportInstanceDTO.getId())
	 * ; if (componentsOptional.isEmpty()) { throw new
	 * ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS +
	 * cannedReport.getId()); } else { List<Components> componentList =
	 * componentsOptional.get(); if (!componentList.isEmpty()) { for (Components
	 * component : componentList) { ReportComponentDTO reportComponent =
	 * populateReportComponent(component); if (null != reportComponent.getActive()
	 * && reportComponent.getActive().equals(CheckType.YES)) {
	 * MessageDetailsFederatedReportInput messageDetailsFederatedReportInput = new
	 * MessageDetailsFederatedReportInput(); messageDetailsFederatedReportInput =
	 * populateBaseInputContext( reportInstanceDTO.getPromptsList());
	 * List<SWIFTMessageDetailsFederatedReportOutput> messageDetails =
	 * processComponents( messageDetailsFederatedReportInput, reportContext,
	 * reportComponent, federatedReportQueryData, reportInstanceDTO); messageData =
	 * populateSwiftDetailedReportData(messageDetails);
	 * reportExecuteResponseCloumnDefList = populateColumnDef(report); } } }
	 * responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
	 * responseData.setData(messageData); } return responseData; }
	 * 
	 * private ReportComponentDTO populateReportComponent(Components component) {
	 * ReportComponentDTO reportComponentDTO = new ReportComponentDTO();
	 * reportComponentDTO.setActive(CheckType.getCheckType(component.getActive()));
	 * reportComponentDTO.setComponentKey(component.getComponentKey());
	 * reportComponentDTO.setComponentName(component.getComponentName());
	 * reportComponentDTO.setId(component.getId());
	 * reportComponentDTO.setReportComponentDetails(populateComponentDetails(
	 * component.getComponentDetailsList())); return reportComponentDTO; }
	 * 
	 * private Set<ReportComponentDetailDTO>
	 * populateComponentDetails(List<ComponentDetails> componentDetailsList) {
	 * Set<ReportComponentDetailDTO> componentDetailDTO = new
	 * HashSet<ReportComponentDetailDTO>();
	 * componentDetailsList.stream().forEach(componentDetails -> {
	 * ReportComponentDetailDTO reportComponentDetailDTO = new
	 * ReportComponentDetailDTO();
	 * reportComponentDetailDTO.setId(componentDetails.getId());
	 * reportComponentDetailDTO.setQuery(componentDetails.getQuery());
	 * reportComponentDetailDTO.setQueryKey(componentDetails.getQueryKey());
	 * reportComponentDetailDTO.setReportComponentId(componentDetails.getComponents(
	 * ).getId()); componentDetailDTO.add(reportComponentDetailDTO); }); return
	 * componentDetailDTO; }
	 * 
	 * private List<ReportExecuteResponseColumnDefDTO> populateColumnDef(Report
	 * reportObject) { List<ReportExecuteResponseColumnDefDTO>
	 * reportExecuteResponseCloumnDefList = new
	 * ArrayList<ReportExecuteResponseColumnDefDTO>(); try { List<Metrics>
	 * metricsList = reportObject.getMetricsList();
	 * metricsList.stream().forEach(metrics -> { ReportExecuteResponseColumnDefDTO
	 * reportExecuteResponseCloumnDef = new ReportExecuteResponseColumnDefDTO();
	 * reportExecuteResponseCloumnDef.setField(metrics.getDisplayName());
	 * reportExecuteResponseCloumnDefList.add(reportExecuteResponseCloumnDef); });
	 * List<String> metricsWithLinkList = prepareLinkReportInfo(reportObject);
	 * reportExecuteResponseCloumnDefList.stream().forEach(colummnDef -> { if
	 * (metricsWithLinkList.contains(colummnDef.getField())) {
	 * colummnDef.setLinkExists(Boolean.TRUE); } });
	 * 
	 * } catch (JpaSystemException exception) { log.error(FILENAME +
	 * " [Exception Occured] " + exception.getMessage()); } catch
	 * (ResourceNotFoundException exception) { log.error(FILENAME +
	 * " [Exception Occured] " + exception.getMessage()); } return
	 * reportExecuteResponseCloumnDefList; }
	 * 
	 * private List<String> prepareLinkReportInfo(Report reportObject) {
	 * List<String> metricsWithLinks = new ArrayList<String>();
	 * List<LinkedReportResponseDTO> linkedreportResponseDTOList = linkReportService
	 * .fetchLinkedReportByReportId(reportObject.getId());
	 * linkedreportResponseDTOList.stream().forEach(linkedreportResponseDTO -> {
	 * metricsWithLinks.add(linkedreportResponseDTO.getSourceMetricName()); });
	 * return metricsWithLinks; }
	 * 
	 * private List<Map<String, Object>> populateSwiftDetailedReportData(
	 * List<SWIFTMessageDetailsFederatedReportOutput> swiftDetailedReports) {
	 * List<Map<String, Object>> swiftDetailedReportDataList = new
	 * ArrayList<Map<String, Object>>(); for
	 * (SWIFTMessageDetailsFederatedReportOutput swiftReport : swiftDetailedReports)
	 * { if (null != swiftReport.getKey() && null != swiftReport.getValue()) {
	 * Map<String, Object> mapData = new HashMap<String, Object>();
	 * mapData.put(MashreqFederatedReportConstants.FIELD_DESCRIPTION,
	 * swiftReport.getKey());
	 * mapData.put(MashreqFederatedReportConstants.FIELD_VALUE,
	 * swiftReport.getValue()); swiftDetailedReportDataList.add(mapData); } } return
	 * swiftDetailedReportDataList; }
	 * 
	 * // private void processMessageDetailsRMesgQuery(ReportComponentDetailDTO
	 * componentDetails, // MessageDetailsFederatedReportInput
	 * messagingDetailsInput, String componentKey, // SwiftDetailsReportObjectDTO
	 * swiftDetailsReportObjectDTO, ReportContext reportContext) { // //
	 * FederatedReportComponentDetailContext context = new
	 * FederatedReportComponentDetailContext(); // List<FederatedReportPromptDTO>
	 * promptsList = new ArrayList<FederatedReportPromptDTO>(); //
	 * List<FederatedReportOutput> flexReportExecuteResponse = new
	 * ArrayList<FederatedReportOutput>(); //
	 * context.setQueryId(componentDetails.getId()); //
	 * context.setQueryKey(componentDetails.getQueryKey()); //
	 * context.setQueryString(componentDetails.getQuery()); //
	 * promptsList.add(messagingDetailsInput.getReferenceNumPrompt()); //
	 * promptsList.add(messagingDetailsInput.getMessageSubFormatPrompt()); //
	 * promptsList.add(messagingDetailsInput.getMessageThroughPrompt()); //
	 * context.setPrompts(promptsList); //
	 * context.setExecutionId(reportContext.getExecutionId()); //
	 * flexReportExecuteResponse =
	 * queryExecutorService.executeQuery(componentDetails, context); //
	 * processMessageDetailsRMesgData(flexReportExecuteResponse,
	 * swiftDetailsReportObjectDTO); // // } // // private ReportComponentDetailDTO
	 * getMatchedComponentDetails(Set<ReportComponentDetailDTO>
	 * componentDetailsList, // String componentKey) { // ReportComponentDetailDTO
	 * componentDetailsObject = componentDetailsList.stream() // .filter(component
	 * ->
	 * componentKey.equalsIgnoreCase(component.getQueryKey())).findAny().orElse(null
	 * ); // return componentDetailsObject; // }
	 * 
	 * private List<SWIFTMessageDetailsFederatedReportOutput> processComponents(
	 * MessageDetailsFederatedReportInput reportInputContext, ReportContext
	 * reportContext, ReportComponentDTO reportInstanceComponentsDTO,
	 * FederatedReportQueryData federatedReportQueryData, ReportInstanceDTO
	 * reportInstanceDTO) { List<SWIFTMessageDetailsFederatedReportOutput> out = new
	 * ArrayList<SWIFTMessageDetailsFederatedReportOutput>(); if (null !=
	 * reportInstanceComponentsDTO) { // pick the right component
	 * FederatedReportPromptDTO messageThroughPrompt =
	 * reportInputContext.getMessageThroughPrompt(); String messageThrough =
	 * messageThroughPrompt.getPromptValue(); if
	 * (messageThrough.equalsIgnoreCase(MashreqFederatedReportConstants.
	 * MESSAGE_THROUGH_SWIFT)) { processSwiftMessage(reportInputContext,
	 * reportContext, reportInstanceComponentsDTO, federatedReportQueryData,
	 * reportInstanceDTO); } else if
	 * (messageThrough.equalsIgnoreCase(MashreqFederatedReportConstants.
	 * MESSAGE_THROUGH_UAEFTS)) { processUaeftsMessage(reportInputContext,
	 * reportContext, reportInstanceComponentsDTO, federatedReportQueryData,
	 * reportInstanceDTO); } } return out; }
	 * 
	 * private void processSwiftMessage(MessageDetailsFederatedReportInput
	 * reportInputContext, ReportContext reportContext, ReportComponentDTO
	 * reportInstanceComponentsDTO, FederatedReportQueryData
	 * federatedReportQueryData, ReportInstanceDTO reportInstanceDTO) {
	 * 
	 * ReportComponentDTO instanceComponent =
	 * getMatchedInstanceComponent(reportInstanceComponentsDTO,
	 * MashreqFederatedReportConstants.MESSAGE_DETAILS_SWIFT_COMPONENT_KEY); if
	 * (instanceComponent != null) {
	 * reportInputContext.setComponent(instanceComponent);
	 * List<SWIFTMessageDetailsFederatedReportOutput> reportComponentData =
	 * swiftDetailedReportService .processMessageDetailsReport(reportInputContext,
	 * reportInstanceComponentsDTO, reportContext); if
	 * (!reportComponentData.isEmpty()) {
	 * populateSWIFTDataToObjectForm(reportComponentData, federatedReportQueryData);
	 * // transformReportData(federatedReportQueryData.getQueryData(),
	 * getCannedReportInstanceRetrievalService() //
	 * .getCannedReportInstanceMetrics(reportInstanceDTO.getId())); } } }
	 * 
	 * private void
	 * populateSWIFTDataToObjectForm(List<SWIFTMessageDetailsFederatedReportOutput>
	 * componentOutput, FederatedReportQueryData federatedReportQueryData) {
	 * List<FederatedReportOutput> data = new ArrayList<FederatedReportOutput>();
	 * for (SWIFTMessageDetailsFederatedReportOutput componentOut : componentOutput)
	 * { SWIFTMessageDetailsFederatedReportOutput output = componentOut;
	 * FederatedReportOutput defaultOutput = createFederatedDefaultOutput(output);
	 * List<Object> rowData = new ArrayList<Object>(); rowData.add(output.getKey());
	 * rowData.add(output.getValue()); defaultOutput.setRowData(rowData);
	 * data.add(defaultOutput); } federatedReportQueryData.setQueryData(data); }
	 * 
	 * public FederatedReportOutput
	 * createFederatedDefaultOutput(SWIFTMessageDetailsFederatedReportOutput
	 * baseOutput) { FederatedReportOutput output = new FederatedReportOutput();
	 * output.setComponentDetailId(baseOutput.getComponentDetailId()); return
	 * output; }
	 * 
	 * public ReportComponentDTO getMatchedInstanceComponent(ReportComponentDTO
	 * component, String compKey) { ReportComponentDTO matchedComponent = null; if
	 * (component.getComponentKey().equalsIgnoreCase(compKey) && CheckType.YES ==
	 * component.getActive()) { matchedComponent = component; } return
	 * matchedComponent; }
	 * 
	 * private void processUaeftsMessage(MessageDetailsFederatedReportInput
	 * reportInputContext, ReportContext reportContext, ReportComponentDTO
	 * cannedReportInstanceComponents, FederatedReportQueryData
	 * federatedReportQueryData, ReportInstanceDTO cannedReportInstance) {
	 * ReportComponentDTO instanceComponent =
	 * getMatchedInstanceComponent(cannedReportInstanceComponents,
	 * MashreqFederatedReportConstants.MESSAGE_DETAILS_UAEFTS_COMPONENT_KEY); if
	 * (instanceComponent != null) {
	 * reportInputContext.setComponent(instanceComponent); //need to check
	 * List<FederatedReportOutput> reportComponentData = null;//
	 * UAEFTSReportService.processUAEFTSReport(reportInputContext, reportContext);
	 * if (!reportComponentData.isEmpty()) { List<FederatedReportOutput>
	 * reportOutput = new ArrayList<FederatedReportOutput>(); if
	 * (!reportComponentData.isEmpty()) { for (FederatedReportOutput componentOut :
	 * reportComponentData) { FederatedReportOutput output = (FederatedReportOutput)
	 * componentOut; reportOutput.add(output); } } // transform the data //
	 * transformReportData(reportOutput, getCannedReportInstanceRetrievalService()
	 * // .getCannedReportInstanceMetrics(cannedReportInstance.getId()));
	 * 
	 * // important that this is populated before transpose reportOutput =
	 * transposeDataElements(reportOutput);
	 * federatedReportQueryData.setQueryData(reportOutput); } } }
	 * 
	 * // protected List<FederatedReportOutput> transposeDataElements
	 * (List<FederatedReportOutput> dataElements) { List<FederatedReportOutput>
	 * outputList = new ArrayList<FederatedReportOutput>(); if
	 * (dataElements.isEmpty()) { return outputList; } // transpose the data with
	 * headers List<List<String>> originalData = new ArrayList<List<String>>(); if
	 * (!dataElements.isEmpty()) { List<String> columnLabels =
	 * dataElements.get(0).getColumnLabels(); originalData.add(0, columnLabels); }
	 * for (FederatedReportOutput tranpOutput : dataElements) {
	 * originalData.add(tranpOutput.getTransformedData()); } int numRows =
	 * originalData.size(); int cols = originalData.get(0).size(); for (int index =
	 * 0; index < cols; index++) { FederatedReportOutput output = new
	 * FederatedReportOutput(); List<String> transposedRow = new
	 * ArrayList<String>(); for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
	 * transposedRow.add(originalData.get(rowIndex).get(index)); }
	 * output.setTransformedData(transposedRow); outputList.add(output); } return
	 * outputList; }
	 * 
	 * private MessageDetailsFederatedReportInput
	 * populateBaseInputContext(List<ReportPromptsInstanceDTO> list) {
	 * 
	 * MessageDetailsFederatedReportInput messageDetailsFederatedReportInput = new
	 * MessageDetailsFederatedReportInput(); FederatedReportPromptDTO
	 * messageThroughPrompt = getMatchedInstancePrompt(list,
	 * MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_THROUGH_PROMPT_KEY);
	 * FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(list,
	 * MashreqFederatedReportConstants.MESSAGE_DETAILS_REFERENCE_NUM_PROMPT_KEY);
	 * FederatedReportPromptDTO messageTypePrompt = getMatchedInstancePrompt(list,
	 * MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_TYPE_PROMPT_KEY);
	 * FederatedReportPromptDTO messageSubFormatFormat =
	 * getMatchedInstancePrompt(list,
	 * MashreqFederatedReportConstants.MESSAGE_DETAILS_MESSAGE_SUB_FORMAT_PROMPT_KEY
	 * ); if (null != messageThroughPrompt) {
	 * messageDetailsFederatedReportInput.setMessageThroughPrompt(
	 * messageThroughPrompt); } if (null != referenceNumPrompt) {
	 * messageDetailsFederatedReportInput.setReferenceNumPrompt(referenceNumPrompt);
	 * } if (null != messageTypePrompt) {
	 * messageDetailsFederatedReportInput.setMessageTypePrompt(messageTypePrompt); }
	 * if (null != messageSubFormatFormat) {
	 * messageDetailsFederatedReportInput.setMessageSubFormatPrompt(
	 * messageSubFormatFormat); } return messageDetailsFederatedReportInput; }
	 * 
	 * private FederatedReportPromptDTO
	 * getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> list, String
	 * promptKey) { FederatedReportPromptDTO federatedReportPromptDTO = new
	 * FederatedReportPromptDTO(); Optional<ReportPromptsInstanceDTO>
	 * promptsOptional = list.stream() .filter(prompts ->
	 * prompts.getPrompt().getKey().equalsIgnoreCase(promptKey)).findAny();
	 * ReportPromptsInstanceDTO reportInstancePrompt = promptsOptional.get(); if
	 * (null != reportInstancePrompt) { List<String> promptsList = new
	 * ArrayList<String>(); if (null != reportInstancePrompt && null !=
	 * reportInstancePrompt.getPrompt().getPromptValue()) {
	 * promptsList.add(reportInstancePrompt.getPrompt().getPromptValue()); } if
	 * (null != reportInstancePrompt &&
	 * !reportInstancePrompt.getPrompt().getValue().isEmpty()) ; {
	 * promptsList.addAll(reportInstancePrompt.getPrompt().getValue()); } String
	 * promptValue = promptsList.stream().collect(Collectors.joining(","));
	 * federatedReportPromptDTO.setPromptKey(reportInstancePrompt.getPrompt().getKey
	 * ()); federatedReportPromptDTO.setPromptValue(promptValue); }
	 * 
	 * return federatedReportPromptDTO; }
	 * 
	 */}
