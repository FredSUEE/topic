import argparse
import requests
import json

URL_PATTERN = "https://api.polyvore.com/1.0/search/set?query={query}"
POLYVORE_URL_PATTERN = "http://www.polyvore.com/cgi/set?id={id}"


def polyvore_search(query, topic_id, output):
    req_url = URL_PATTERN.format(query=query)
    res = requests.get(req_url).json()
    items = res['content']['items']

    with open(output, 'w') as output_file:
        count = 0
        for item in items:
            count += 1
            if count == 10:
                break
            media_url = item['img_urls']['y']
            text = item['title']
            original_url = POLYVORE_URL_PATTERN.format(id=item['id'])
            data = {
                'topic_id': topic_id,
                'text': text,
                'media_url': media_url,
                'original_url': original_url,
            }
            json.dump(data, output_file)
            output_file.write('\n')


if __name__ == "__main__":
    import sys

    parser = argparse.ArgumentParser(
        description="Polyvore Set Search Script")
    parser.add_argument("--q", help="Search term", default="Google")
    parser.add_argument("--topic", help="Topic ID")
    parser.add_argument("--output", help="Output file")
    args = parser.parse_args(sys.argv[1:])

    polyvore_search(args.q, args.topic, args.output)