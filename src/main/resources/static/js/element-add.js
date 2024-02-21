function getTermInputs() {
    const result = [];
    result.push(createType("TERM"));
    // content

    const content = document.createElement("label");
    content.classList.add("form-control", "mt-3");
    let labelText = document.createTextNode("Content: ");
    content.appendChild(labelText);
    let input = document.createElement("input");
    content.appendChild(input);
    input.setAttribute("name", "content");
    input.setAttribute("required", "required");
    input.setAttribute("placeholder", "Term eingeben");
    result.push(content);
    // structure
    result.push(createStructureId());
    // button
    result.push(createSubmitButton("Term"));
    return result
}

function getDefinitionInputs() {
    const result = [];
    result.push(createType("DEFINITION"));
    // content
    result.push(createTextArea());
    // structure
    result.push(createStructureId());
    // button
    result.push(createSubmitButton("Definition"));
    return result
}

function getFactInputs() {
    const result = [];
    result.push(createType("FACT"));
    // content
    result.push(createTextArea());
    // structure
    result.push(createStructureId());
    // button
    result.push(createSubmitButton("Fakt"));
    return result
}

function getCodeInputs() {
    const result = [];
    result.push(createType("CODE"));
    // language
    const language = document.createElement("label");
    language.classList.add("form-control", "mt-3");
    let labelText = document.createTextNode("Language: ");
    language.appendChild(labelText);
    let input = document.createElement("input");
    language.appendChild(input);
    input.setAttribute("type", "text");
    input.setAttribute("name", "language");
    input.setAttribute("placeholder", "Sprache eingeben");
    result.push(language);
    result.push(createHeadline());
    // content
    result.push(createTextArea());
    // structure
    result.push(createStructureId());
    // button
    result.push(createSubmitButton("Codeschnipsel"));
    return result

}

function getTrueFalseInputs() {
    let result = [];
    result.push(createItemType("TRUE_FALSE"));
    result.push(createTextArea());
    const label = document.createElement("label");
    label.classList.add("form-control", "mt-3");
    const isTrue = document.createElement("input");
    isTrue.setAttribute("type", "checkbox");
    isTrue.setAttribute("name", "isTrue");
    isTrue.setAttribute("value", "True");
    label.appendChild(isTrue);
    label.append("Ist korrekt!");
    result.push(label);
    return result;
}

function getSingleChoiceInputs() {
    let result = [];
    result.push(createItemType("TRUE_FALSE"));
    result.push(createTextArea());
    // correct answer
    const correct = document.createElement("label");
    correct.classList.add("form-control", "mt-3");
    let labelText = document.createTextNode("Correct Answer: ");
    correct.appendChild(labelText);
    let input = document.createElement("input");
    correct.appendChild(input);
    input.setAttribute("type", "text");
    input.setAttribute("name", "correctAnswers");
    input.setAttribute("placeholder", "Korrekte Antwort eingeben");
    correct.appendChild(input);
    result.push(correct);

    // wrong answers
    const wrong = document.createElement("label");
    wrong.classList.add("form-control", "mt-3");
    wrong.setAttribute("id", "wrong-answers");
    let wrongText = document.createTextNode("Wrong Answers: ");
    wrong.appendChild(wrongText);
    let wrongInput = document.createElement("input");
    wrong.appendChild(wrongInput);
    wrongInput.setAttribute("type", "text");
    wrongInput.setAttribute("name", "wrongAnswers");
    wrongInput.setAttribute("placeholder", "Falsche Antwort eingeben");
    wrong.appendChild(wrongInput);
    result.push(wrong);
    // add button
    const addButton = document.createElement("button");
    addButton.setAttribute("type", "button");
    addButton.classList.add("btn", "btn-warning", "mt-3");
    addButton.setAttribute("onclick", "addWrongAnswer()");
    addButton.appendChild(document.createTextNode("Falsche Antwort hinzufügen"));
    result.push(addButton);
    // submit
    result.push(createSubmitButton("Single Choice"));
    return result;
}

function addWrongAnswer() {
    const wrong = document.getElementById("wrong-answers");
    const input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("name", "wrongAnswers");
    input.setAttribute("placeholder", "Falsche Antwort eingeben");
    wrong.appendChild(input);

}

function loadItemMask(select) {
    const type = select.value;
    const form = document.getElementById("item-content");
    let result;
    switch (type) {
        case "TRUE_FALSE":
            result = getTrueFalseInputs();
            break;
        case "MULTIPLE_CHOICE":
            result = getMultipleChoiceInputs();
            break;
        case "SINGLE_CHOICE":
            result = getSingleChoiceInputs();
            break;
        case "NONE":
            return;
        default:
            form.innerHTML = "FALSCHER TYP";
    }
    result.forEach(input => form.appendChild(input));
}

function getItemTypes() {
    const result = [];
    const select = document.createElement("select");
    select.setAttribute("name", "itemType");
    select.setAttribute("onchange", "loadItemMask(this)");
    result.push(select);
    const empty = document.createElement("option");
    empty.setAttribute("value", "NONE");
    select.add(empty);
    const tfOption = document.createElement("option");
    tfOption.setAttribute("value", "TRUE_FALSE");
    tfOption.appendChild(document.createTextNode("Wahr/Falsch"));
    select.add(tfOption);
    const mcOption = document.createElement("option");
    mcOption.setAttribute("value", "MULTIPLE_CHOICE");
    mcOption.appendChild(document.createTextNode("Multiple Choice"));
    select.add(mcOption);
    const scOption = document.createElement("option");
    scOption.setAttribute("value", "SINGLE_CHOICE");
    scOption.appendChild(document.createTextNode("Single Choice"));
    select.add(scOption);
    return result;
}

function getItemInputs() {
    const result = [];
    let types = getItemTypes();
    types.forEach(input => result.push(input));
    result.push(createType("ITEM"));
    // content
    const div = document.createElement("div");
    div.classList.add("form-control", "mt-3");
    div.setAttribute("id", "item-content");
    result.push(div);
    // short Name
    let shortName = document.createElement("label");
    shortName.classList.add("form-control", "mt-3");
    let labelText = document.createTextNode("Short Name: ");
    let input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("name", "headline");
    input.setAttribute("placeholder", "Short Name eingeben");
    input.setAttribute("required", "required")
    shortName.appendChild(labelText);
    shortName.appendChild(input);
    result.push(shortName);
    // structure
    result.push(createStructureId());
    // button
    result.push(createSubmitButton("Item"));
    return result
}

function loadElementMask(select) {
    const type = select.value;
    const form = document.getElementById("element-add-form");
    form.innerHTML = "";
    let result;
    switch (type) {
        case "TEXT":
            result = getTextInputs();
            break;
        case "TERM":
            result = getTermInputs();
            break;
        case "DEFINITION":
            result = getDefinitionInputs();
            break;
        case "FACT":
            result = getFactInputs();
            break;
        case "CODE":
            result = getCodeInputs();
            break;
        case "ITEM":
            result = getItemInputs();
            break;
        default:
            form.innerHTML = "FALSCHER TYP";
    }
    result.forEach(input => form.appendChild(input));
}

function getTextInputs() {
    const result = [];
    result.push(createType("TEXT"));
    result.push(createHeadline());
    // content
    result.push(createTextArea());
    // structure
    result.push(createStructureId());
    // button
    result.push(createSubmitButton("Text"));
    return result
}


function createStructureId() {
    const structure = document.createElement("label");
    structure.classList.add("form-control", "mt-3");
    let labelText = document.createTextNode("Structure-Id: ");
    structure.appendChild(labelText);
    let input = document.createElement("input");
    structure.appendChild(input);
    input.setAttribute("type", "text");
    input.setAttribute("name", "structureId");
    input.setAttribute("placeholder", "Structure-Id eingeben");
    input.setAttribute("list", "structure-list");
    return structure;
}

function createType(typeName) {
    const type = document.createElement("input");
    type.setAttribute("type", "hidden");
    type.setAttribute("name", "type");
    type.setAttribute("value", typeName);
    return type;
}

function createItemType(typeName) {
    const type = document.createElement("input");
    type.setAttribute("type", "hidden");
    type.setAttribute("name", "itemType");
    type.setAttribute("value", typeName);
    return type;
}

function createHeadline() {
    const headline = document.createElement("label");
    let labelText = document.createTextNode("Headline: ");
    headline.appendChild(labelText);
    headline.classList.add("form-control", "mt-3");
    let input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("name", "headline");
    input.setAttribute("placeholder", "Headline eingeben");
    headline.appendChild(input);
    return headline;
}

function createSubmitButton(elementName = "Element") {
    const submit = document.createElement("input");
    submit.setAttribute("type", "submit");
    submit.setAttribute("value", elementName + " erstellen");
    submit.classList.add("btn", "btn-primary", "mt-3");
    return submit;
}

function createTextArea() {
    const content = document.createElement("label");
    content.classList.add("form-control", "mt-3");
    let labelText = document.createTextNode("Content: ");
    content.appendChild(labelText);
    let input = document.createElement("textarea");
    content.appendChild(input);
    input.setAttribute("name", "content");
    input.setAttribute("placeholder", "Text eingeben");
    input.setAttribute("rows", "4");
    input.setAttribute("cols", "80");
    return content;
}
