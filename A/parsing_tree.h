#pragma once

#include "rules.h"

using namespace std;

template <typename Tag = default_tag>
class parsing_tree
{
    rules<Tag> input_rules;

public:
    bool set_input_rules(string str)
    {
        return input_rules.parse(str);
    }
};
