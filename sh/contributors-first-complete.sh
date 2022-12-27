#!/bin/sh
#
# contributors-all-complete.sh
#
# Get complete information about the first contributor to a GitHub project.

user=lampepfl
repo=dotty
uri="https://api.github.com/repos/$user/$repo/contributors"

curl -s $uri | jq '.[0]'
