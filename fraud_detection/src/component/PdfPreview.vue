<template>
<div id="container">
  <div id="viewer" class="pdfViewer">

  </div>
</div>
</template>

<script>
import pdfjsLib from "pdfjs-dist/build/pdf"
import {PDFViewer} from "pdfjs-dist/web/pdf_viewer"
import "pdfjs-dist/web/pdf_viewer.css"
import pdfjsWorker from 'pdfjs-dist/build/pdf.worker.entry';
pdfjsLib.GlobalWorkerOptions.workerSrc = pdfjsWorker;
export default {
  name: 'PdfViewer',
  props: {
    pdfUrl: {
      type: String,
      required: true
    }
  },
  mounted() {
    this.getPdf();
  },
  methods: {
    async getPdf() {
      let container = document.getElementById("container")
      let pdfViewer = new PDFViewer({
        container: container
      })
      let loadingTask = pdfjsLib.getDocument("http://localhost:8000/download")
      let pdf = await loadingTask.promise
      pdfViewer.setDocument(pdf)
    }
  }
}
</script>

<style scoped>

</style>
