import openai
import pathlib
import sys
from gpt4all import GPT4All

# list of models to choose from
model_names = ["mistral-7b-openorca.Q4_0.gguf", "nous-hermes-llama2-13b.Q4_0.gguf"]

# global variables
global_prompt = None
global_connection_type = None
global_model_name = None
global_url = None
global_api_key = None


def parse_args():
    global global_prompt, global_connection_type, global_model_name, global_url
    if len(sys.argv) < 2:
        print("Usage: python main.py promt [local|remote] [model_name] [url] [api_key]")
        exit(2)
    global_prompt = sys.argv[1]
    global_connection_type = sys.argv[2] if len(sys.argv) > 2 else "local"
    global_model_name = sys.argv[3] if len(sys.argv) > 3 else None
    global_url = sys.argv[4] if len(sys.argv) > 4 else None
    global_api_key = sys.argv[5] if len(sys.argv) > 5 else None


def local(prompt, model_name):
    model_dir = pathlib.Path().joinpath("../model-files/")
    model_name = model_names[0] if model_name is None else model_name
    model = GPT4All(model_name, model_path=model_dir, device="cpu")
    output = model.generate(prompt, max_tokens=3)
    with model.chat_session():
        print(output)
        while True:
            user_input = input("You: ")
            if user_input == "quit":
                break
            response = model.generate(user_input)
            print(f"Bot: {response}")


def remote(prompt, model_name, url: str = None, api_key: str = None):
    openai.api_base = "http://localhost:4891/v1" if url is None else url
    # Set up the prompt and other parameters for the API request
    model = model_names[0].removesuffix("Q4_0.gguf") if model_name is None else model_name
    openai.api_key = "not needed for a local LLM" if api_key is None else api_key

    response = openai.Completion.create(
        model=model,
        prompt=prompt,
        max_tokens=100,
        temperature=0.28,
        top_p=0.95,
        n=1,
        echo=False,
        stream=False
    )

    # Make the API request new openapi-Version (1.0+)
    """response = openai.OpenAI().chat.completions.create(
        messages=[
            {
                "role": "user",
                "content": "What is the capital of France?"
            }
        ],
        model=model,
    )"""

    # Print the generated completion
    print(response)


if __name__ == '__main__':
    parse_args()
    if global_connection_type == "local":
        local(global_prompt, global_model_name)
    else:
        remote(global_prompt, global_model_name, url=global_url, api_key=global_api_key)
