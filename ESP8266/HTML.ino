const PROGMEM char *ROOT_HTML = /*html*/R"=====(
<style>
  * {
    margin: 0px;
    padding: 0px;
    box-sizing: border-box;
  }

  html, body {
    width: 100vw;
    height: 100vh;
    background: #212121;
    color: #efefef;
  }

  body {
    display: flex;
    place-items: center;
    place-content: center;
  }

  td {
    padding: 10px;
  }

  td * {
    width: 100%;
  }
</style>

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<table>
  <tr>
    <td>Brightness</td>
    <td><input id="brightness" type="range" min="0" max="255" step="1"></td>
  </tr>
  <tr>
    <td>Speed</td>
    <td><input id="speed" type="range" min="1" max="10" step="0.5"></td>
  </tr>
  <tr>
    <td>Color</td>
    <td><input id="color" type="color"></td>
  </tr>
  <tr>
    <td>Animation</td>
    <td>
      <select id="animation" type="color">
        <option value=0>Rainbow cycle</value>
        <option value=1>Rainbow</value>
        <option value=2>Solid color</value>
        <option value=3>Knight rider</value>
        <option value=4>Sparkle</value>
        <option value=5>Pulse</value>
        <option value=6>Fire</value>
      </select>
    </td>
  </tr>
</table>

<script>
  let ids = ["brightness", "speed", "color", "animation"];
  let elements = ids.map(id => document.getElementById(id));
  elements.forEach(x => x.onchange = sendValue.bind(null, x.id));

  function sendValue(id) {
    let body = new FormData();
    body.append("value", document.getElementById(id).value.toString().replace("#", ""));

    fetch("/" + id, {
      method: "POST",
      body
    });
  }
</script>
)=====";