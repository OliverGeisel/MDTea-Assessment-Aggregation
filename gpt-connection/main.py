import argparse
import openai
import pathlib
import requests
import sys
from gpt4all import GPT4All


def check_server_is_availability(url) -> bool:
    try:
        requests.post(url)
    except requests.ConnectionError:
        print(f"Webseite {url} is not available")
        return False
    except requests.RequestException:
        return False
    return True


class PromptParameters:

    def __init__(self, p, r, t, m):
        self.p = p
        self.r = r
        self.t = t
        self.m = m

    @staticmethod
    def empty():
        return PromptParameters(.4, 1, .7, 200)


# list of models to choose from
model_names = ["mistral-7b-openorca.Q4_0.gguf", "nous-hermes-llama2-13b.Q4_0.gguf", "gpt4all-falcon-newbpe-q4_0.gguf"]

# global variables
global_prompt: str = None
global_connection_type: str = None
global_model_name: str = None
global_url: str = None
global_api_key: str = None
global_prompt_parameters = PromptParameters.empty()


def parse_args():
    global global_prompt, global_connection_type, global_model_name, \
        global_url, global_api_key, global_prompt_parameters
    with open("args.txt", "w") as f:
        for num, arg in enumerate(sys.argv):
            f.write(f"{num} " + arg + "\n\n")
    parser = argparse.ArgumentParser(description='GPT-Connection')
    parser.add_argument('-p', type=float, help='Wert für -p (topP)', default=0.95, required=False)
    parser.add_argument('-r', type=int, help='Wert für -r (retries)', default=1, required=False)
    parser.add_argument('-t', type=float, help='Wert für -t (temperature)', default=0.28, required=False)
    parser.add_argument('-m', type=int, help='Wert für -m (max_tokens))', default=100, required=False)
    parser.add_argument('normal', type=str, nargs='+', help='Prompt')

    args = parser.parse_args()
    global_prompt_parameters = PromptParameters(args.p, args.r, args.t, args.m)
    main_arguments = args.normal
    if len(main_arguments) < 1:
        print("Usage: python main.py prompt [local|remote] [model_name] [url] [api_key]")
        exit(99)
    global_prompt = main_arguments[0]
    global_connection_type = main_arguments[1] if len(main_arguments) > 1 else "local"
    global_model_name = main_arguments[2] if len(main_arguments) > 2 else None
    global_url = main_arguments[3] if len(main_arguments) > 3 else None
    global_api_key = main_arguments[4] if len(main_arguments) > 4 else None


def local(prompt, model_name):
    model_dir = pathlib.Path().joinpath("../model-files/")
    model_name = model_names[0] if model_name is None else model_name
    model = GPT4All(model_name, model_path=model_dir, device="cpu")
    output = model.generate(prompt, max_tokens=global_prompt_parameters.m, temp=global_prompt_parameters.t,
                            top_p=global_prompt_parameters.p)
    print(output)
    """with model.chat_session():
        
        while True:
            user_input = input("You: ")
            if user_input == "quit":
                break
            response = model.generate(user_input)
            print(f"Bot: {response}")"""


def remote(prompt, model_name, url: str = None, api_key: str = None):
    correct_url = "http://localhost:4891/v1" if url is None or not url.startswith("http") else url
    # Set up the prompt and other parameters for the API request
    if not check_server_is_availability(correct_url):
        print("Server isn't available")
        exit(3)  # Server isn't available
    openai.api_base = correct_url
    model = model_names[0].removesuffix(".gguf").removesuffix(
        ".Q4_0") if model_name is None or model_name.strip() == "" else model_name
    openai.api_key = "not needed for a local LLM" if api_key is None or api_key.strip() == "" else api_key

    response = openai.Completion.create(
        model=model,
        prompt=prompt,
        max_tokens=global_prompt_parameters.m,
        temperature=global_prompt_parameters.t,
        top_p=global_prompt_parameters.p,
        n=global_prompt_parameters.r,
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
    with open("last-response.txt", "w") as f:
        f.write(str(response))


if __name__ == '__main__':
    parse_args()
    if global_connection_type == "local":
        local(global_prompt, global_model_name)
    else:
        remote(global_prompt, global_model_name, url=global_url, api_key=global_api_key)
