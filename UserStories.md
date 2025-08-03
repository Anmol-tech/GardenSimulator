## User Stories

### Epic 1: Garden Management
**As a gardener,**
- **US1.1**: I want to plant different types of plants so that I can create a diverse garden
- **US1.2**: I want to see the health status of my plants so that I can monitor their well-being
- **US1.3**: I want to water all my plants at once so that I can efficiently maintain them
- **US1.4**: I want to see which plants are infested with pests so that I can take action

### Epic 2: Environmental Control
**As a gardener,**
- **US2.1**: I want to trigger sunny day events so that I can test how my plants handle heat stress
- **US2.2**: I want to trigger rainy day events so that I can see how rainfall affects my garden
- **US2.3**: I want to trigger pest infestations so that I can test my garden's resilience
- **US2.4**: I want to trigger perfect growing conditions so that I can see optimal plant growth

### Epic 3: Automation
**As a gardener,**
- **US3.1**: I want to enable automation so that my garden can run without constant supervision
- **US3.2**: I want to see automation status so that I know when the system is actively managing my garden
- **US3.3**: I want to disable automation so that I can take manual control when needed

### Epic 4: Monitoring and Analytics
**As a gardener,**
- **US4.1**: I want to see real-time statistics so that I can track garden performance
- **US4.2**: I want to view activity logs so that I can understand what happened in my garden
- **US4.3**: I want to see hourly summaries so that I can track long-term garden health
- **US4.4**: I want to clear logs so that I can start fresh monitoring

### Epic 5: System Integration
**As a system administrator,**
- **US5.1**: I want to initialize the garden programmatically so that I can set up automated testing
- **US5.2**: I want to retrieve plant metadata so that I can understand garden configuration
- **US5.3**: I want to trigger environmental events via API so that I can simulate various conditions
- **US5.4**: I want to get garden state summaries so that I can monitor system performance

### Epic 6: User Experience
**As a user,**
- **US6.1**: I want to see visual plant representations so that I can easily identify different plants
- **US6.2**: I want to see color-coded health indicators so that I can quickly assess plant status
- **US6.3**: I want to see formatted pest names so that I can easily read infestation reports
- **US6.4**: I want to see styled event messages so that I can quickly understand what's happening

## Acceptance Criteria

### For Garden Management
- [ ] User can select and plant any of the 5 plant types
- [ ] Plant health is displayed with color-coded indicators (green=healthy, yellow=medium, orange=low, red=pest)
- [ ] Water All button waters all living plants and shows count
- [ ] Dead plants are automatically replaced after 3 cycles

### For Environmental Events
- [ ] Sunny day increases temperature and causes extra drying
- [ ] Rainy day waters all plants and shows count
- [ ] Pest infestation affects vulnerable plants based on predefined lists
- [ ] Perfect growth conditions restore temperature and water plants twice

### For Automation
- [ ] Automation toggle starts/stops automatic garden updates
- [ ] Automation runs every 3 seconds when enabled
- [ ] Pest spray automatically deploys when pests are detected
- [ ] Random events trigger during automation

### For Logging
- [ ] All events are logged with timestamps and emojis
- [ ] Hourly summaries are written to daily log files
- [ ] Real-time statistics are displayed in UI
- [ ] Logs can be cleared by user action