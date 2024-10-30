import { Viewer } from "@bytemd/react";
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import "github-markdown-css/github-markdown-light.css";
import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";
import "./index.css";
import { useEffect, useState } from "react";

interface Props {
  value?: string;
}

const plugins = [gfm(), highlight()];

/**
 * Markdown 浏览器
 * @param props
 * @constructor
 */
const MdViewer = (props: Props) => {
  const [content, setContent] = useState("");
  
  useEffect(() => {
    if (props.value) {
      setContent(props.value);
    }
  }, [props.value]);

  return (
    <div className="md-viewer">
      <Viewer value={content} plugins={plugins} />
    </div>
  );
};

export default MdViewer;
