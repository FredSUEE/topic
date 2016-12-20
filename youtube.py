#!/usr/bin/python

from apiclient.discovery import build
from apiclient.errors import HttpError
from oauth2client.tools import argparser

import json

# Set DEVELOPER_KEY to the API key value from the APIs & auth > Registered apps
# tab of
#   https://cloud.google.com/console
# Please ensure that you have enabled the YouTube Data API for your project.
DEVELOPER_KEY = "AIzaSyAck07Kr33sDlSJKKl_zaBJYzV7K2ws-Bc"
YOUTUBE_API_SERVICE_NAME = "youtube"
YOUTUBE_API_VERSION = "v3"

YOUTUBE_URL_PATTERN = "https://www.youtube.com/watch?v={vid}"


def youtube_search(options):
    youtube = build(YOUTUBE_API_SERVICE_NAME, YOUTUBE_API_VERSION,
                    developerKey=DEVELOPER_KEY)

    # Call the search.list method to retrieve results matching the specified
    # query term.
    search_response = youtube.search().list(
        q=options.q,
        part="id,snippet",
        maxResults=options.max_results
    ).execute()

    with open(options.output, 'w') as outfile:
        for search_result in search_response.get("items", []):
            if search_result["id"]["kind"] == "youtube#video":
                title = search_result["snippet"]["title"]
                video_id = search_result["id"]["videoId"]
                data = {
                    'topic_id': options.topic,
                    'text': title,
                    'media_url': YOUTUBE_URL_PATTERN.format(vid=video_id),
                    'original_url': YOUTUBE_URL_PATTERN.format(vid=video_id),
                }
                json.dump(data, outfile)
                outfile.write("\n")


if __name__ == "__main__":
    argparser.add_argument("--q", help="Search term", default="Google")
    argparser.add_argument("--max-results", help="Max results", default=25)
    argparser.add_argument("--topic", help="topic id")
    argparser.add_argument("--output", help="Output file")
    args = argparser.parse_args()

    try:
        youtube_search(args)
    except HttpError, e:
        print e
        print "An HTTP error %d occurred:\n%s" % (e.resp.status, e.content)
