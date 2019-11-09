if (redis.call('exists', KEYS[1]) == 1) then
    local stock = tonumber(redis.call('get', KEYS[1]));
    local num = tonumber(ARGV[1]);
    if (stock == -1) then
        return -1;
    end;
    if (stock >= num) then
        return redis.call('incrbyfloat', KEYS[1], 0 - num);
    end;
    return -2;
end;
return -3;