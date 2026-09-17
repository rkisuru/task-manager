{{/*
Common labels applied to all resources.
*/}}
{{- define "taskflow.labels" -}}
app.kubernetes.io/part-of: taskflow
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end -}}

{{/*
Selector labels for a specific component.
Usage: {{ include "taskflow.selectorLabels" (dict "app" "user-service") }}
*/}}
{{- define "taskflow.selectorLabels" -}}
app: {{ .app }}
{{- end -}}
